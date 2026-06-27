package com.example.labmanagement.controller.admin;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.CategoryService;
import com.example.labmanagement.service.CurrentUserService;
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
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public AdminCategoryController(CategoryService categoryService, CurrentUserService currentUserService) {
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long editId,
            Model model
    ) {
        model.addAttribute("categories", categoryService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("formCategory", editId == null ? new Category() : categoryService.findById(editId));
        return "admin/categories";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Category formCategory,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            categoryService.save(formCategory, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "分类保存成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
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
            categoryService.delete(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "分类删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "分类正在被设备使用，不能删除");
        }
        return "redirect:/admin/categories";
    }
}
