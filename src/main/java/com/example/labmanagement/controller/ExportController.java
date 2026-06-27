package com.example.labmanagement.controller;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.CurrentUserService;
import com.example.labmanagement.service.ExcelExportService;
import com.example.labmanagement.web.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

@Controller
public class ExportController {

    private final ExcelExportService exportService;
    private final CurrentUserService currentUserService;

    public ExportController(ExcelExportService exportService, CurrentUserService currentUserService) {
        this.exportService = exportService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/admin/export/equipment")
    public ResponseEntity<byte[]> exportEquipment(HttpSession session, HttpServletRequest request) {
        UserAccount current = currentUserService.get(session);
        byte[] data = exportService.exportEquipment(current, RequestUtils.clientIp(request));
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("设备信息.xlsx", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
