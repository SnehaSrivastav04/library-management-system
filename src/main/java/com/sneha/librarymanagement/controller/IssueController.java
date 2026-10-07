package com.sneha.librarymanagement.controller;

import com.sneha.librarymanagement.dto.IssueRequest;
import com.sneha.librarymanagement.dto.IssueResponse;
import com.sneha.librarymanagement.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponse> issueBook(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issueBook(request));
    }
}