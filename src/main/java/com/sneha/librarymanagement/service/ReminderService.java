package com.sneha.librarymanagement.service;

import com.sneha.librarymanagement.entity.IssueRecord;
import com.sneha.librarymanagement.entity.IssueStatus;
import com.sneha.librarymanagement.repository.IssueRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("5.00");

    private final IssueRecordRepository issueRecordRepository;
    private final EmailService emailService;

    // Runs automatically every day (time comes from reminder.cron in application.properties)
    @Scheduled(cron = "${reminder.cron}", zone = "Asia/Kolkata")
    public void runDaily() {
        int sent = sendReminders();
        log.info("Daily reminder job finished: {} email(s) sent", sent);
    }

    /** Sends "due tomorrow" reminders and "overdue" notices. Returns how many emails were sent. */
    public int sendReminders() {
        LocalDate today = LocalDate.now();
        int sent = 0;

        for (IssueRecord r : issueRecordRepository.findDueOn(IssueStatus.ISSUED, today.plusDays(1))) {
            String body = """
                    Hi %s,

                    This is a reminder that "%s" is due tomorrow (%s).
                    Please return it on time to avoid a fine of Rs. 5 per day.

                    Library Management System
                    """.formatted(r.getUser().getName(), r.getBook().getTitle(), r.getDueDate());
            if (emailService.send(r.getUser().getEmail(), "Reminder: book due tomorrow", body)) {
                sent++;
            }
        }

        for (IssueRecord r : issueRecordRepository.findOverdue(IssueStatus.ISSUED, today)) {
            long daysLate = ChronoUnit.DAYS.between(r.getDueDate(), today);
            BigDecimal fine = FINE_PER_DAY.multiply(BigDecimal.valueOf(daysLate));
            String body = """
                    Hi %s,

                    "%s" was due on %s and is now %d day(s) overdue.
                    Fine so far: Rs. %s. Please return it as soon as possible.

                    Library Management System
                    """.formatted(r.getUser().getName(), r.getBook().getTitle(), r.getDueDate(), daysLate, fine);
            if (emailService.send(r.getUser().getEmail(), "Overdue: please return your book", body)) {
                sent++;
            }
        }

        return sent;
    }
}