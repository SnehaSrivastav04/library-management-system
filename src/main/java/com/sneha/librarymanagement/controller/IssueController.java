package com.sneha.librarymanagement.controller;

import com.sneha.librarymanagement.dto.IssueRequest;
import com.sneha.librarymanagement.dto.IssueResponse;
import com.sneha.librarymanagement.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponse> issueBook(@Valid @RequestBody IssueRequest request,
                                                   @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.issueBook(request, principal.getUsername()));
    }

    @PutMapping("/{id}/return")
    public IssueResponse returnBook(@PathVariable Long id) {
        return issueService.returnBook(id);
    }

    @GetMapping("/my")
    public List<IssueResponse> getMyIssues(@AuthenticationPrincipal UserDetails principal) {
        return issueService.getMyIssues(principal.getUsername());
    }

    @GetMapping("/user/{userId}")
    public List<IssueResponse> getIssuesByUser(@PathVariable Long userId,
                                               @AuthenticationPrincipal UserDetails principal) {
        return issueService.getIssuesByUser(userId, principal.getUsername());
    }
}