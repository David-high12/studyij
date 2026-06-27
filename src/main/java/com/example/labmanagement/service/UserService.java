package com.example.labmanagement.service;

import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService logService;

    public UserService(UserAccountRepository userRepository, PasswordEncoder passwordEncoder, OperationLogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    public List<UserAccount> findAll(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return userRepository.findAll();
        }
        String value = keyword.trim();
        return userRepository.findByUsernameContainingIgnoreCaseOrRealNameContainingIgnoreCase(value, value);
    }

    public UserAccount findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @Transactional
    public UserAccount save(UserAccount form, String rawPassword, UserAccount operator, String ipAddress) {
        UserAccount user = form.getId() == null ? new UserAccount() : findById(form.getId());
        if (form.getId() == null && userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (form.getId() != null && !user.getUsername().equals(form.getUsername()) && userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        user.setUsername(form.getUsername().trim());
        user.setRealName(form.getRealName().trim());
        user.setPhone(form.getPhone());
        user.setRole(form.getRole() == null ? Role.USER : form.getRole());
        user.setStatus(form.getStatus() == null ? UserStatus.ENABLED : form.getStatus());

        if (form.getId() == null) {
            String password = StringUtils.hasText(rawPassword) ? rawPassword : "123456";
            user.setPassword(passwordEncoder.encode(password));
        } else if (StringUtils.hasText(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        UserAccount saved = userRepository.save(user);
        logService.record(operator, "用户管理", "保存用户：" + saved.getUsername(), ipAddress);
        return saved;
    }

    @Transactional
    public void resetPassword(Long id, UserAccount operator, String ipAddress) {
        UserAccount user = findById(id);
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        logService.record(operator, "用户管理", "重置密码：" + user.getUsername(), ipAddress);
    }

    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        UserAccount user = findById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度至少为6位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id, UserAccount operator, String ipAddress) {
        UserAccount user = findById(id);
        userRepository.delete(user);
        logService.record(operator, "用户管理", "删除用户：" + user.getUsername(), ipAddress);
    }
}
