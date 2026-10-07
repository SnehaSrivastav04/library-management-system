package com.sneha.librarymanagement.repository;

import com.sneha.librarymanagement.entity.IssueRecord;
import com.sneha.librarymanagement.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, IssueStatus status);

    List<IssueRecord> findByUserId(Long userId);
}