package com.sneha.librarymanagement.repository;

import com.sneha.librarymanagement.entity.IssueRecord;
import com.sneha.librarymanagement.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, IssueStatus status);

    List<IssueRecord> findByUserId(Long userId);

    // join fetch loads user and book in the same query, so emails can be built without lazy-loading errors
    @Query("select r from IssueRecord r join fetch r.user join fetch r.book "
            + "where r.status = :status and r.dueDate = :dueDate")
    List<IssueRecord> findDueOn(@Param("status") IssueStatus status, @Param("dueDate") LocalDate dueDate);

    @Query("select r from IssueRecord r join fetch r.user join fetch r.book "
            + "where r.status = :status and r.dueDate < :date")
    List<IssueRecord> findOverdue(@Param("status") IssueStatus status, @Param("date") LocalDate date);
}