package com.example.labmanagement.repository;

import com.example.labmanagement.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findTop200ByOrderByOperationTimeDesc();

    List<OperationLog> findByOperationTypeContainingIgnoreCaseAndOperationTimeBetweenOrderByOperationTimeDesc(
            String operationType,
            LocalDateTime start,
            LocalDateTime end
    );
}
