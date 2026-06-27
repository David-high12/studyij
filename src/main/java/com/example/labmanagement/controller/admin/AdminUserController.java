package com.example.labmanagement.controller.admin;

import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.service.CurrentUserService;
import com.example.labmanagement.service.UserService;
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
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AdminUserController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long editId,
            Model model
    ) {
        model.addAttribute("users", userService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("formUser", editId == null ? new UserAccount() : userService.findById(editId));
        return "admin/users";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute UserAccount formUser,
            @RequestParam(required = false) String rawPassword,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            userService.save(formUser, rawPassword, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "用户保存成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/reset")
    public String resetPassword(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            userService.resetPassword(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "密码已重置为 123456");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        UserAccount current = currentUserService.get(session);
        if (current.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "不能删除当前登录用户");
            return "redirect:/admin/users";
        }
        try {
            userService.delete(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "用户删除成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
