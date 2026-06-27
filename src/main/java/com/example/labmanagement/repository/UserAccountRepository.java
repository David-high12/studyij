package com.example.labmanagement.repository;

import com.example.labmanagement.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserAccount> findByUsernameContainingIgnoreCaseOrRealNameContainingIgnoreCase(String username, String realName);
}
