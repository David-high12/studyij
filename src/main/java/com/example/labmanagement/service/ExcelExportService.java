package com.example.labmanagement.service;

import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.EquipmentRepository;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    private final EquipmentRepository equipmentRepository;
    private final OperationLogService logService;

    public ExcelExportService(EquipmentRepository equipmentRepository, OperationLogService logService) {
        this.equipmentRepository = equipmentRepository;
        this.logService = logService;
    }

    public byte[] exportEquipment(UserAccount operator, String ipAddress) {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("设备信息");
            String[] headers = {"设备编号", "设备名称", "分类", "型号", "总数量", "可借数量", "状态", "存放位置", "备注"};

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            for (int i = 0; i < equipmentList.size(); i++) {
                Equipment equipment = equipmentList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(equipment.getEquipmentCode());
                row.createCell(1).setCellValue(equipment.getEquipmentName());
                row.createCell(2).setCellValue(equipment.getCategory().getCategoryName());
                row.createCell(3).setCellValue(nullToEmpty(equipment.getModel()));
                row.createCell(4).setCellValue(equipment.getTotalQuantity());
                row.createCell(5).setCellValue(equipment.getAvailableQuantity());
                row.createCell(6).setCellValue(equipment.getStatus().getLabel());
                row.createCell(7).setCellValue(nullToEmpty(equipment.getLocation()));
                row.createCell(8).setCellValue(nullToEmpty(equipment.getRemark()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            logService.record(operator, "数据导出", "导出设备信息", ipAddress);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("导出设备信息失败", e);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
