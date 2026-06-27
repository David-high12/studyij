package com.example.labmanagement.service;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.repository.EquipmentRepository;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExcelExportServiceTest {

    private final EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    private final OperationLogService logService = mock(OperationLogService.class);
    private final ExcelExportService exportService = new ExcelExportService(equipmentRepository, logService);

    @Test
    void exportEquipmentCreatesReadableWorkbook() throws Exception {
        when(equipmentRepository.findAll()).thenReturn(List.of(equipment()));

        byte[] bytes = exportService.exportEquipment(null, "127.0.0.1");

        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            var sheet = workbook.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("设备编号");
            assertThat(sheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo("实验笔记本");
            assertThat(sheet.getRow(1).getCell(6).getStringCellValue()).isEqualTo("可借用");
        }
    }

    private Equipment equipment() {
        Category category = new Category();
        category.setCategoryName("计算机设备");

        Equipment equipment = new Equipment();
        equipment.setEquipmentCode("EQ-001");
        equipment.setEquipmentName("实验笔记本");
        equipment.setCategory(category);
        equipment.setModel("ThinkBook");
        equipment.setTotalQuantity(5);
        equipment.setAvailableQuantity(3);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setLocation("A101");
        equipment.setRemark("课程实验");
        return equipment;
    }
}
