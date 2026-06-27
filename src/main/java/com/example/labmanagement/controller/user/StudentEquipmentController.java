package com.example.labmanagement.controller.user;

import com.example.labmanagement.service.CategoryService;
import com.example.labmanagement.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentEquipmentController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;

    public StudentEquipmentController(EquipmentService equipmentService, CategoryService categoryService) {
        this.equipmentService = equipmentService;
        this.categoryService = categoryService;
    }

    @GetMapping("/equipment")
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        model.addAttribute("equipmentList", equipmentService.findBorrowable(keyword, categoryId));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "equipment/list";
    }
}
