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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final int LOAN_DAYS = 14;

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
}