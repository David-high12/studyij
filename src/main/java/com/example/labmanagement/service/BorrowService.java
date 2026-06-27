package com.example.labmanagement.service;

import com.example.labmanagement.entity.BorrowRecord;
import com.example.labmanagement.entity.BorrowStatus;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.BorrowRecordRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import com.example.labmanagement.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRepository;
    private final UserAccountRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final OperationLogService logService;

    public BorrowService(
            BorrowRecordRepository borrowRepository,
            UserAccountRepository userRepository,
            EquipmentRepository equipmentRepository,
            OperationLogService logService
    ) {
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.logService = logService;
    }

    public List<BorrowRecord> findAll() {
        return borrowRepository.findAllByOrderByCreateTimeDesc();
    }

    public List<BorrowRecord> findByUser(Long userId) {
        return borrowRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        if (status == null) {
            return findAll();
        }
        return borrowRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public BorrowRecord findById(Long id) {
        return borrowRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("借用记录不存在"));
    }

    @Transactional
    public BorrowRecord applyBorrow(Long userId, Long equipmentId, Integer quantity, LocalDate expectedReturnTime, String remark) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("借用数量至少为1");
        }
        UserAccount user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        if (!equipment.canBorrow(quantity)) {
            throw new IllegalArgumentException("设备当前不可借或库存不足");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setEquipment(equipment);
        record.setBorrowQuantity(quantity);
        record.setExpectedReturnTime(expectedReturnTime);
        record.setRemark(remark);
        record.setStatus(BorrowStatus.PENDING);
        return borrowRepository.save(record);
    }

    @Transactional
    public BorrowRecord approveBorrow(Long recordId, UserAccount operator, String ipAddress) {
        BorrowRecord record = findById(recordId);
        if (record.getStatus() != BorrowStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核记录可以审核通过");
        }
        Equipment equipment = record.getEquipment();
        if (!equipment.canBorrow(record.getBorrowQuantity())) {
            throw new IllegalArgumentException("库存不足，无法审核通过");
        }

        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - record.getBorrowQuantity());
        if (equipment.getAvailableQuantity() == 0) {
            equipment.setStatus(EquipmentStatus.BORROWED);
        }
        // 审核通过才真正扣减库存，保证待审核申请不会提前占用设备。
        equipmentRepository.save(equipment);

        record.setStatus(BorrowStatus.BORROWED);
        record.setBorrowTime(LocalDateTime.now());
        BorrowRecord saved = borrowRepository.save(record);
        logService.record(operator, "借用管理", "审核通过：" + equipment.getEquipmentName(), ipAddress);
        return saved;
    }

    @Transactional
    public BorrowRecord rejectBorrow(Long recordId, UserAccount operator, String ipAddress) {
        BorrowRecord record = findById(recordId);
        if (record.getStatus() != BorrowStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核记录可以拒绝");
        }
        record.setStatus(BorrowStatus.REJECTED);
        BorrowRecord saved = borrowRepository.save(record);
        logService.record(operator, "借用管理", "拒绝借用：" + record.getEquipment().getEquipmentName(), ipAddress);
        return saved;
    }

    @Transactional
    public BorrowRecord returnBorrow(Long recordId, UserAccount operator, String ipAddress) {
        BorrowRecord record = findById(recordId);
        if (record.getStatus() != BorrowStatus.BORROWED) {
            throw new IllegalArgumentException("只有已借出记录可以归还");
        }

        Equipment equipment = record.getEquipment();
        int restored = equipment.getAvailableQuantity() + record.getBorrowQuantity();
        equipment.setAvailableQuantity(Math.min(restored, equipment.getTotalQuantity()));
        if (equipment.getStatus() == EquipmentStatus.BORROWED && equipment.getAvailableQuantity() > 0) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
        }
        // 归还时恢复可借数量，避免库存长期被已归还记录占用。
        equipmentRepository.save(equipment);

        record.setStatus(BorrowStatus.RETURNED);
        record.setActualReturnTime(LocalDateTime.now());
        BorrowRecord saved = borrowRepository.save(record);
        logService.record(operator, "借用管理", "归还设备：" + equipment.getEquipmentName(), ipAddress);
        return saved;
    }
}
