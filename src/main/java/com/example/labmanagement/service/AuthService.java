package com.example.labmanagement.service;

import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserAccount> login(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }

        return userRepository.findByUsername(username.trim())
                .filter(user -> user.getStatus() == UserStatus.ENABLED)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }
}
