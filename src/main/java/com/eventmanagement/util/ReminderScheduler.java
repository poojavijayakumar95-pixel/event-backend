package com.eventmanagement.util;

import com.eventmanagement.entity.Registration;
import com.eventmanagement.enums.RegistrationStatus;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    /**
     * Runs every day at 9 AM — sends reminder emails for events happening tomorrow.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReminders() {
        LocalDateTime tomorrowStart = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0);
        LocalDateTime tomorrowEnd = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59);

        log.info("Running reminder scheduler for events between {} and {}", tomorrowStart, tomorrowEnd);

        eventRepository.findUpcomingEventsInRange(tomorrowStart, tomorrowEnd).forEach(event -> {
            List<Registration> registrations =
                    registrationRepository.findByEventIdAndStatus(event.getId(), RegistrationStatus.REGISTERED);

            registrations.forEach(reg -> {
                try {
                    emailService.sendEventReminder(reg);
                } catch (Exception e) {
                    log.error("Failed to send reminder for registration {}: {}", reg.getId(), e.getMessage());
                }
            });

            log.info("Sent {} reminders for event: {}", registrations.size(), event.getTitle());
        });
    }
}
