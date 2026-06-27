package com.example.labmanagement.service;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.CategoryRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final CategoryRepository categoryRepository;
    private final OperationLogService logService;

    public EquipmentService(EquipmentRepository equipmentRepository, CategoryRepository categoryRepository, OperationLogService logService) {
        this.equipmentRepository = equipmentRepository;
        this.categoryRepository = categoryRepository;
        this.logService = logService;
    }

    public List<Equipment> findAll(String keyword, Long categoryId, EquipmentStatus status) {
        Specification<Equipment> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("equipmentName")), pattern),
                        builder.like(builder.lower(root.get("equipmentCode")), pattern)
                ));
            }
            if (categoryId != null) {
                predicates.add(builder.equal(root.get("category").get("id"), categoryId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return equipmentRepository.findAll(specification);
    }

    public List<Equipment> findBorrowable(String keyword, Long categoryId) {
        return findAll(keyword, categoryId, EquipmentStatus.AVAILABLE).stream()
                .filter(equipment -> equipment.getAvailableQuantity() != null && equipment.getAvailableQuantity() > 0)
                .toList();
    }

    public Equipment findById(Long id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
    }

    @Transactional
    public Equipment save(Equipment form, Long categoryId, UserAccount operator, String ipAddress) {
        if (form.getAvailableQuantity() > form.getTotalQuantity()) {
            throw new IllegalArgumentException("可借数量不能大于总数量");
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        Equipment equipment = form.getId() == null ? new Equipment() : findById(form.getId());
        if (form.getId() == null && equipmentRepository.existsByEquipmentCode(form.getEquipmentCode())) {
            throw new IllegalArgumentException("设备编号已存在");
        }
        if (form.getId() != null && !equipment.getEquipmentCode().equals(form.getEquipmentCode()) && equipmentRepository.existsByEquipmentCode(form.getEquipmentCode())) {
            throw new IllegalArgumentException("设备编号已存在");
        }

        equipment.setEquipmentName(form.getEquipmentName().trim());
        equipment.setEquipmentCode(form.getEquipmentCode().trim());
        equipment.setCategory(category);
        equipment.setModel(form.getModel());
        equipment.setTotalQuantity(form.getTotalQuantity());
        equipment.setAvailableQuantity(form.getAvailableQuantity());
        equipment.setStatus(form.getStatus() == null ? EquipmentStatus.AVAILABLE : form.getStatus());
        equipment.setLocation(form.getLocation());
        equipment.setRemark(form.getRemark());

        Equipment saved = equipmentRepository.save(equipment);
        logService.record(operator, "设备管理", "保存设备：" + saved.getEquipmentName(), ipAddress);
        return saved;
    }

    @Transactional
    public void delete(Long id, UserAccount operator, String ipAddress) {
        Equipment equipment = findById(id);
        equipmentRepository.delete(equipment);
        logService.record(operator, "设备管理", "删除设备：" + equipment.getEquipmentName(), ipAddress);
    }
}
