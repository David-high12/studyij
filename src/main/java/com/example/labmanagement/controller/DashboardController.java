package com.example.labmanagement.controller;

import com.example.labmanagement.entity.BorrowStatus;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.repository.BorrowRecordRepository;
import com.example.labmanagement.repository.CategoryRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import com.example.labmanagement.repository.UserAccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final UserAccountRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowRecordRepository borrowRepository;

    public DashboardController(
            UserAccountRepository userRepository,
            CategoryRepository categoryRepository,
            EquipmentRepository equipmentRepository,
            BorrowRecordRepository borrowRepository
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.equipmentRepository = equipmentRepository;
        this.borrowRepository = borrowRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@RequestParam(required = false) Boolean forbidden, Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("equipmentCount", equipmentRepository.count());
        model.addAttribute("pendingCount", borrowRepository.findByStatusOrderByCreateTimeDesc(BorrowStatus.PENDING).size());
        model.addAttribute("borrowableCount", equipmentRepository.findByStatus(EquipmentStatus.AVAILABLE).size());
        model.addAttribute("forbidden", Boolean.TRUE.equals(forbidden));
        return "dashboard";
    }
}
