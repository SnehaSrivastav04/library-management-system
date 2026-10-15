package com.sneha.librarymanagement.dto;

import com.sneha.librarymanagement.entity.Role;
import com.sneha.librarymanagement.entity.User;

public record UserResponse(Long id, String name, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}