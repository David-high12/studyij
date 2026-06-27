package com.example.labmanagement.controller;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.CurrentUserService;
import com.example.labmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final UserService userService;

    public ProfileController(CurrentUserService currentUserService, UserService userService) {
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    @GetMapping("/profile/password")
    public String passwordPage() {
        return "profile/password";
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            userService.changePassword(current.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "密码修改成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile/password";
    }
}
