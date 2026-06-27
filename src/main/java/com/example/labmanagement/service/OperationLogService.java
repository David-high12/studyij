package com.example.labmanagement.service;

import com.example.labmanagement.entity.OperationLog;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.repository.OperationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogRepository logRepository;

    public OperationLogService(OperationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void record(UserAccount user, String type, String content, String ipAddress) {
        OperationLog log = new OperationLog();
        log.setUser(user);
        log.setOperationType(type);
        log.setOperationContent(content);
        log.setIpAddress(ipAddress);
        logRepository.save(log);
    }

    public List<OperationLog> findRecent() {
        return logRepository.findTop200ByOrderByOperationTimeDesc();
    }

    public List<OperationLog> search(String operationType, LocalDate startDate, LocalDate endDate) {
        String type = operationType == null ? "" : operationType.trim();
        LocalDate start = startDate == null ? LocalDate.now().minusYears(5) : startDate;
        LocalDate end = endDate == null ? LocalDate.now().plusDays(1) : endDate;
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        return logRepository.findByOperationTypeContainingIgnoreCaseAndOperationTimeBetweenOrderByOperationTimeDesc(
                type,
                startTime,
                endTime
        );
    }
}
