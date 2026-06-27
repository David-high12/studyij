package com.example.labmanagement.controller.admin;

import com.example.labmanagement.service.OperationLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {

    private final OperationLogService logService;

    public AdminLogController(OperationLogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model
    ) {
        boolean hasFilter = operationType != null || startDate != null || endDate != null;
        model.addAttribute("logs", hasFilter ? logService.search(operationType, startDate, endDate) : logService.findRecent());
        model.addAttribute("operationType", operationType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/logs";
    }
}
