package com.example.labmanagement.controller.user;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.BorrowService;
import com.example.labmanagement.service.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class StudentBorrowController {

    private final BorrowService borrowService;
    private final CurrentUserService currentUserService;

    public StudentBorrowController(BorrowService borrowService, CurrentUserService currentUserService) {
        this.borrowService = borrowService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/borrow/apply")
    public String apply(
            @RequestParam Long equipmentId,
            @RequestParam Integer quantity,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expectedReturnTime,
            @RequestParam(required = false) String remark,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserAccount current = currentUserService.get(session);
            borrowService.applyBorrow(current.getId(), equipmentId, quantity, expectedReturnTime, remark);
            redirectAttributes.addFlashAttribute("success", "借用申请已提交，请等待管理员审核");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipment";
    }

    @GetMapping("/my/borrows")
    public String myBorrows(HttpSession session, Model model) {
        UserAccount current = currentUserService.get(session);
        model.addAttribute("records", borrowService.findByUser(current.getId()));
        return "borrow/my";
    }
}
