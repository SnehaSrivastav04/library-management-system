package com.sneha.librarymanagement.controller;

import com.sneha.librarymanagement.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReminderService reminderService;

    @PostMapping("/reminders/run")
    public Map<String, Integer> runReminders() {
        return Map.of("emailsSent", reminderService.sendReminders());
    }
}