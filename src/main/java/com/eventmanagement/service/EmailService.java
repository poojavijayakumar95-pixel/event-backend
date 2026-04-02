package com.eventmanagement.service;

import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.Registration;
import com.eventmanagement.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy 'at' hh:mm a");

    @Async("asyncExecutor")
    public void sendRegistrationConfirmation(Registration registration) {
        User user = registration.getUser();
        Event event = registration.getEvent();
        String subject = "Registration Confirmed: " + event.getTitle();
        String body = buildRegistrationEmail(user, event);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    @Async("asyncExecutor")
    public void sendRegistrationCancellation(Registration registration) {
        User user = registration.getUser();
        Event event = registration.getEvent();
        String subject = "Registration Cancelled: " + event.getTitle();
        String body = buildCancellationEmail(user, event);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    @Async("asyncExecutor")
    public void sendEventReminder(Registration registration) {
        User user = registration.getUser();
        Event event = registration.getEvent();
        String subject = "Reminder: " + event.getTitle() + " is tomorrow!";
        String body = buildReminderEmail(user, event);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    @Async("asyncExecutor")
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to EventHub!";
        String body = buildWelcomeEmail(user);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} | Subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildRegistrationEmail(User user, Event event) {
        return htmlWrapper(
            "<h2 style='color:#2563eb'>Registration Confirmed! 🎉</h2>" +
            "<p>Hi " + user.getFirstName() + ",</p>" +
            "<p>You're all set for <strong>" + event.getTitle() + "</strong>.</p>" +
            eventDetailsBlock(event) +
            "<a href='" + frontendUrl + "/events/" + event.getId() + "' style='" + btnStyle() + "'>View Event</a>" +
            "<p style='color:#6b7280;font-size:13px'>You can cancel your registration from your dashboard any time before the event.</p>"
        );
    }

    private String buildCancellationEmail(User user, Event event) {
        return htmlWrapper(
            "<h2 style='color:#dc2626'>Registration Cancelled</h2>" +
            "<p>Hi " + user.getFirstName() + ",</p>" +
            "<p>Your registration for <strong>" + event.getTitle() + "</strong> has been cancelled.</p>" +
            "<p>We hope to see you at future events.</p>" +
            "<a href='" + frontendUrl + "/events' style='" + btnStyle() + "'>Browse Events</a>"
        );
    }

    private String buildReminderEmail(User user, Event event) {
        return htmlWrapper(
            "<h2 style='color:#d97706'>Event Reminder ⏰</h2>" +
            "<p>Hi " + user.getFirstName() + ",</p>" +
            "<p>Just a reminder that <strong>" + event.getTitle() + "</strong> is tomorrow!</p>" +
            eventDetailsBlock(event) +
            "<a href='" + frontendUrl + "/events/" + event.getId() + "' style='" + btnStyle() + "'>View Details</a>"
        );
    }

    private String buildWelcomeEmail(User user) {
        return htmlWrapper(
            "<h2 style='color:#2563eb'>Welcome to EventHub! 👋</h2>" +
            "<p>Hi " + user.getFirstName() + ",</p>" +
            "<p>Your account has been created. Start exploring upcoming events!</p>" +
            "<a href='" + frontendUrl + "/events' style='" + btnStyle() + "'>Browse Events</a>"
        );
    }

    private String eventDetailsBlock(Event event) {
        return "<div style='background:#f3f4f6;border-radius:8px;padding:16px;margin:16px 0'>" +
               "<p style='margin:4px 0'><strong>📅 Date:</strong> " + event.getStartDateTime().format(FORMATTER) + "</p>" +
               "<p style='margin:4px 0'><strong>📍 Location:</strong> " + event.getLocation() + "</p>" +
               (event.getVenue() != null ? "<p style='margin:4px 0'><strong>🏢 Venue:</strong> " + event.getVenue() + "</p>" : "") +
               "</div>";
    }

    private String btnStyle() {
        return "display:inline-block;padding:12px 24px;background:#2563eb;color:white;" +
               "border-radius:6px;text-decoration:none;font-weight:600;margin:16px 0";
    }

    private String htmlWrapper(String content) {
        return "<!DOCTYPE html><html><body style='font-family:sans-serif;max-width:600px;margin:0 auto;padding:20px;color:#1f2937'>" +
               "<div style='border-bottom:3px solid #2563eb;padding-bottom:12px;margin-bottom:24px'>" +
               "<h1 style='color:#2563eb;margin:0'>EventHub</h1></div>" +
               content +
               "<hr style='margin:32px 0;border:none;border-top:1px solid #e5e7eb'/>" +
               "<p style='color:#9ca3af;font-size:12px'>© EventHub. You received this email because you registered on our platform.</p>" +
               "</body></html>";
    }
}
