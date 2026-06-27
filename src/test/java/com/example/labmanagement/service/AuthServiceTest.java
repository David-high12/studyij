package com.example.labmanagement.service;

import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserAccountRepository userRepository = mock(UserAccountRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthService authService = new AuthService(userRepository, passwordEncoder);

    @Test
    void loginReturnsEnabledUserWhenPasswordMatches() {
        UserAccount user = user("admin520", "admin1314", UserStatus.ENABLED);
        when(userRepository.findByUsername("admin520")).thenReturn(Optional.of(user));

        Optional<UserAccount> result = authService.login("admin520", "admin1314");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void loginRejectsWrongPassword() {
        UserAccount user = user("admin520", "admin1314", UserStatus.ENABLED);
        when(userRepository.findByUsername("admin520")).thenReturn(Optional.of(user));

        Optional<UserAccount> result = authService.login("admin520", "bad-password");

        assertThat(result).isEmpty();
    }

    @Test
    void loginRejectsDisabledUser() {
        UserAccount user = user("admin520", "admin1314", UserStatus.DISABLED);
        when(userRepository.findByUsername("admin520")).thenReturn(Optional.of(user));

        Optional<UserAccount> result = authService.login("admin520", "admin1314");

        assertThat(result).isEmpty();
    }

    private UserAccount user(String username, String rawPassword, UserStatus status) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRealName("系统管理员");
        user.setRole(Role.ADMIN);
        user.setStatus(status);
        return user;
    }
}
