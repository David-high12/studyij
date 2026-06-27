package com.example.labmanagement.controller.admin;

import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.CategoryService;
import com.example.labmanagement.service.CurrentUserService;
import com.example.labmanagement.service.EquipmentService;
import com.example.labmanagement.web.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/equipment")
public class AdminEquipmentController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public AdminEquipmentController(
            EquipmentService equipmentService,
            CategoryService categoryService,
            CurrentUserService currentUserService
    ) {
        this.equipmentService = equipmentService;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) Long editId,
            Model model
    ) {
        Equipment formEquipment = editId == null ? new Equipment() : equipmentService.findById(editId);
        model.addAttribute("equipmentList", equipmentService.findAll(keyword, categoryId, status));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("statuses", EquipmentStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("formEquipment", formEquipment);
        model.addAttribute("formCategoryId", formEquipment.getCategory() == null ? null : formEquipment.getCategory().getId());
        return "admin/equipment";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Equipment formEquipment,
            @RequestParam Long categoryId,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            equipmentService.save(formEquipment, categoryId, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "设备保存成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/equipment";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            equipmentService.delete(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "设备删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "设备存在借用记录，不能删除");
        }
        return "redirect:/admin/equipment";
    }
}
