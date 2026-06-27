package com.example.labmanagement.controller;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.service.AuthService;
import com.example.labmanagement.service.OperationLogService;
import com.example.labmanagement.web.RequestUtils;
import com.example.labmanagement.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;
    private final OperationLogService logService;

    public AuthController(AuthService authService, OperationLogService logService) {
        this.authService = authService;
        this.logService = logService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute(SessionKeys.LOGIN_USER_ID) != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpSession session,
            Model model
    ) {
        Optional<UserAccount> user = authService.login(username, password);
        if (user.isEmpty()) {
            model.addAttribute("error", "用户名或密码错误，或用户已被停用");
            model.addAttribute("username", username);
            return "login";
        }

        UserAccount loginUser = user.get();
        session.setAttribute(SessionKeys.LOGIN_USER_ID, loginUser.getId());
        session.setAttribute(SessionKeys.LOGIN_USERNAME, loginUser.getUsername());
        session.setAttribute(SessionKeys.LOGIN_ROLE, loginUser.getRole());
        session.setAttribute(SessionKeys.LOGIN_REAL_NAME, loginUser.getRealName());
        logService.record(loginUser, "用户登录", "登录系统", RequestUtils.clientIp(request));
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
