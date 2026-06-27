package com.example.labmanagement.service;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.repository.CategoryRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EquipmentServiceTest {

    private final EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final OperationLogService logService = mock(OperationLogService.class);
    private final EquipmentService equipmentService = new EquipmentService(equipmentRepository, categoryRepository, logService);

    @Test
    void saveRejectsAvailableQuantityGreaterThanTotal() {
        Equipment equipment = equipment();

        assertThatThrownBy(() -> equipmentService.save(equipment, 1L, null, "127.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("可借数量不能大于总数量");

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void saveRejectsDuplicatedEquipmentCodeWhenCreating() {
        Equipment equipment = equipment();
        equipment.setTotalQuantity(5);
        equipment.setAvailableQuantity(2);
        when(equipmentRepository.existsByEquipmentCode("EQ-001")).thenReturn(true);

        assertThatThrownBy(() -> equipmentService.save(equipment, 1L, null, "127.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("设备编号已存在");
    }

    private Equipment equipment() {
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("计算机设备");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("实验笔记本");
        equipment.setEquipmentCode("EQ-001");
        equipment.setTotalQuantity(1);
        equipment.setAvailableQuantity(2);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        return equipment;
    }
}
