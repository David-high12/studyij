package com.example.labmanagement.repository;

import com.example.labmanagement.entity.BorrowRecord;
import com.example.labmanagement.entity.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<BorrowRecord> findByStatusOrderByCreateTimeDesc(BorrowStatus status);

    List<BorrowRecord> findAllByOrderByCreateTimeDesc();
}
