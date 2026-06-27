package com.example.labmanagement.config;

import com.example.labmanagement.entity.Role;
import com.example.labmanagement.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.LOGIN_USER_ID) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Object role = session.getAttribute(SessionKeys.LOGIN_ROLE);
        if (path.startsWith(request.getContextPath() + "/admin") && role != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/dashboard?forbidden=true");
            return false;
        }
        return true;
    }
}
