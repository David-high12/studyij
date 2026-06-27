package com.example.labmanagement.service;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.UserAccountRepository;
import com.example.labmanagement.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository userRepository;

    public CurrentUserService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccount get(HttpSession session) {
        Object userId = session.getAttribute(SessionKeys.LOGIN_USER_ID);
        if (!(userId instanceof Long id)) {
            throw new IllegalStateException("当前用户未登录");
        }
        return userRepository.findById(id).orElseThrow(() -> new IllegalStateException("当前用户不存在"));
    }
}
