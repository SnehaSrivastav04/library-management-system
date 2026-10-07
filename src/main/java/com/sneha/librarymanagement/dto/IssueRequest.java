package com.sneha.librarymanagement.dto;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(
        @NotNull(message = "bookId is required") Long bookId,
        @NotNull(message = "userId is required") Long userId
) {}