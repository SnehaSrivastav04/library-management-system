package com.sneha.librarymanagement.dto;

import com.sneha.librarymanagement.entity.IssueRecord;
import com.sneha.librarymanagement.entity.IssueStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IssueResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long userId,
        String userName,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BigDecimal fineAmount,
        IssueStatus status
) {
    public static IssueResponse from(IssueRecord r) {
        return new IssueResponse(
                r.getId(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getUser().getId(),
                r.getUser().getName(),
                r.getIssueDate(),
                r.getDueDate(),
                r.getReturnDate(),
                r.getFineAmount(),
                r.getStatus()
        );
    }
}