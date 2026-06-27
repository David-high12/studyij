package com.example.labmanagement.controller.admin;

import com.example.labmanagement.entity.BorrowStatus;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.BorrowService;
import com.example.labmanagement.service.CurrentUserService;
import com.example.labmanagement.web.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/borrows")
public class AdminBorrowController {

    private final BorrowService borrowService;
    private final CurrentUserService currentUserService;

    public AdminBorrowController(BorrowService borrowService, CurrentUserService currentUserService) {
        this.borrowService = borrowService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) BorrowStatus status, Model model) {
        model.addAttribute("records", borrowService.findByStatus(status));
        model.addAttribute("statuses", BorrowStatus.values());
        model.addAttribute("status", status);
        return "admin/borrows";
    }

    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            borrowService.approveBorrow(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "借用申请已通过");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            borrowService.rejectBorrow(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "借用申请已拒绝");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @PostMapping("/{id}/return")
    public String returnBorrow(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            borrowService.returnBorrow(id, current, RequestUtils.clientIp(request));
            redirectAttributes.addFlashAttribute("success", "设备归还成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrows";
    }
}
