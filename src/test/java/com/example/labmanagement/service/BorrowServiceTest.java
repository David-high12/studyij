package com.example.labmanagement.service;

import com.example.labmanagement.entity.BorrowRecord;
import com.example.labmanagement.entity.BorrowStatus;
import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.repository.BorrowRecordRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import com.example.labmanagement.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BorrowServiceTest {

    private final BorrowRecordRepository borrowRepository = mock(BorrowRecordRepository.class);
    private final UserAccountRepository userRepository = mock(UserAccountRepository.class);
    private final EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    private final OperationLogService logService = mock(OperationLogService.class);
    private final BorrowService borrowService = new BorrowService(borrowRepository, userRepository, equipmentRepository, logService);

    @Test
    void applyBorrowCreatesPendingRecordWithoutReducingStock() {
        UserAccount user = user(2L);
        Equipment equipment = equipment(5);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(equipmentRepository.findById(10L)).thenReturn(Optional.of(equipment));
        when(borrowRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRecord record = borrowService.applyBorrow(2L, 10L, 2, LocalDate.now().plusDays(7), "课程实验");

        assertThat(record.getStatus()).isEqualTo(BorrowStatus.PENDING);
        assertThat(record.getBorrowQuantity()).isEqualTo(2);
        assertThat(equipment.getAvailableQuantity()).isEqualTo(5);
    }

    @Test
    void approveBorrowReducesStockAndMarksRecordBorrowed() {
        Equipment equipment = equipment(3);
        BorrowRecord record = record(equipment, 2, BorrowStatus.PENDING);
        when(borrowRepository.findById(20L)).thenReturn(Optional.of(record));
        when(borrowRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRecord approved = borrowService.approveBorrow(20L, user(1L), "127.0.0.1");

        assertThat(approved.getStatus()).isEqualTo(BorrowStatus.BORROWED);
        assertThat(equipment.getAvailableQuantity()).isEqualTo(1);
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
        verify(equipmentRepository).save(equipment);
    }

    @Test
    void approveBorrowRejectsInsufficientStock() {
        Equipment equipment = equipment(1);
        BorrowRecord record = record(equipment, 2, BorrowStatus.PENDING);
        when(borrowRepository.findById(20L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> borrowService.approveBorrow(20L, user(1L), "127.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    void returnBorrowRestoresStockAndMarksReturned() {
        Equipment equipment = equipment(0);
        equipment.setStatus(EquipmentStatus.BORROWED);
        BorrowRecord record = record(equipment, 2, BorrowStatus.BORROWED);
        when(borrowRepository.findById(20L)).thenReturn(Optional.of(record));
        when(borrowRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRecord returned = borrowService.returnBorrow(20L, user(1L), "127.0.0.1");

        assertThat(returned.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(equipment.getAvailableQuantity()).isEqualTo(2);
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
    }

    private UserAccount user(Long id) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setUsername(id == 1L ? "admin520" : "user520");
        user.setRealName(id == 1L ? "管理员" : "学生");
        user.setRole(id == 1L ? Role.ADMIN : Role.USER);
        user.setStatus(UserStatus.ENABLED);
        return user;
    }

    private Equipment equipment(int availableQuantity) {
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("计算机设备");

        Equipment equipment = new Equipment();
        equipment.setId(10L);
        equipment.setEquipmentName("实验笔记本");
        equipment.setEquipmentCode("EQ-001");
        equipment.setCategory(category);
        equipment.setTotalQuantity(5);
        equipment.setAvailableQuantity(availableQuantity);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        return equipment;
    }

    private BorrowRecord record(Equipment equipment, int quantity, BorrowStatus status) {
        BorrowRecord record = new BorrowRecord();
        record.setId(20L);
        record.setUser(user(2L));
        record.setEquipment(equipment);
        record.setBorrowQuantity(quantity);
        record.setExpectedReturnTime(LocalDate.now().plusDays(7));
        record.setStatus(status);
        return record;
    }
}
