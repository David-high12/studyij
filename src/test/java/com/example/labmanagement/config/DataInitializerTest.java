package com.example.labmanagement.config;

import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.CategoryRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import com.example.labmanagement.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataInitializerTest {

    private final UserAccountRepository userRepository = mock(UserAccountRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final DataInitializer initializer = new DataInitializer(
            userRepository,
            categoryRepository,
            equipmentRepository,
            passwordEncoder
    );

    @Test
    void seedsDefaultUsersWithUpdatedUsernames() {
        when(userRepository.count()).thenReturn(0L);
        when(categoryRepository.count()).thenReturn(1L);
        ArgumentCaptor<UserAccount> users = ArgumentCaptor.forClass(UserAccount.class);

        initializer.run();

        verify(userRepository, times(2)).save(users.capture());
        List<UserAccount> savedUsers = users.getAllValues();
        assertThat(savedUsers)
                .extracting(UserAccount::getUsername)
                .containsExactly("admin520", "user520");
        assertThat(savedUsers)
                .extracting(UserAccount::getRole)
                .containsExactly(Role.ADMIN, Role.USER);
        assertThat(passwordEncoder.matches("admin1314", savedUsers.get(0).getPassword())).isTrue();
        assertThat(passwordEncoder.matches("user1314", savedUsers.get(1).getPassword())).isTrue();
    }

    @Test
    void renamesExistingDefaultUsersOnStartup() {
        UserAccount admin = user("admin", Role.ADMIN);
        UserAccount student = user("student", Role.USER);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findByUsername("admin520")).thenReturn(Optional.empty(), Optional.empty());
        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(userRepository.findByUsername("user520")).thenReturn(Optional.empty(), Optional.empty());
        when(userRepository.count()).thenReturn(2L);
        when(categoryRepository.count()).thenReturn(1L);

        initializer.run();

        assertThat(admin.getUsername()).isEqualTo("admin520");
        assertThat(student.getUsername()).isEqualTo("user520");
        assertThat(passwordEncoder.matches("admin1314", admin.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("user1314", student.getPassword())).isTrue();
        verify(userRepository).save(admin);
        verify(userRepository).save(student);
    }

    private UserAccount user(String username, Role role) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("old-password"));
        return user;
    }
}
