package com.sneha.librarymanagement.service;

import com.sneha.librarymanagement.dto.IssueRequest;
import com.sneha.librarymanagement.dto.IssueResponse;
import com.sneha.librarymanagement.entity.*;
import com.sneha.librarymanagement.repository.BookRepository;
import com.sneha.librarymanagement.repository.IssueRecordRepository;
import com.sneha.librarymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final int LOAN_DAYS = 14;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("5.00");

    private final IssueRecordRepository issueRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssueResponse issueBook(IssueRequest request) {
        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book not found with id " + request.bookId()));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id " + request.userId()));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account is inactive");
        }
        if (user.getRole() != Role.MEMBER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Books can only be issued to members");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No copies available for this book");
        }
        if (issueRecordRepository.existsByBookIdAndUserIdAndStatus(book.getId(), user.getId(), IssueStatus.ISSUED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This member has already issued this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDate today = LocalDate.now();
        IssueRecord record = IssueRecord.builder()
                .book(book)
                .user(user)
                .issueDate(today)
                .dueDate(today.plusDays(LOAN_DAYS))
                .status(IssueStatus.ISSUED)
                .build();

        return IssueResponse.from(issueRecordRepository.save(record));
    }

    @Transactional
    public IssueResponse returnBook(Long issueId) {
        IssueRecord record = issueRecordRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Issue record not found with id " + issueId));

        if (record.getStatus() == IssueStatus.RETURNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This book has already been returned");
        }

        Book book = bookRepository.findByIdForUpdate(record.getBook().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        LocalDate today = LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), today);
        BigDecimal fine = overdueDays > 0
                ? FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays))
                : BigDecimal.ZERO;

        record.setReturnDate(today);
        record.setFineAmount(fine);
        record.setStatus(IssueStatus.RETURNED);
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return IssueResponse.from(issueRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByUser(Long userId) {
        return issueRecordRepository.findByUserId(userId).stream()
                .map(IssueResponse::from)
                .toList();
    }
}