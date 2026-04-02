package com.eventmanagement.controller;

import com.eventmanagement.dto.request.AuthRequest;
import com.eventmanagement.dto.request.EventRequest;
import com.eventmanagement.dto.request.RegistrationRequest;
import com.eventmanagement.entity.User;
import com.eventmanagement.enums.EventCategory;
import com.eventmanagement.enums.Role;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.eventmanagement.util.ReminderScheduler;
import com.eventmanagement.service.EmailService;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests covering the full user and admin workflows.
 * Uses H2 in-memory DB. JavaMailSender is mocked so no real emails are sent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// FIX: force H2 so @SpringBootTest doesn't try to connect to real MySQL
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:e2etest;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=VALUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("End-to-End User Workflow Tests")
class UserWorkflowE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Mock mail sender — no real SMTP connection attempted
    @MockBean private JavaMailSender javaMailSender;

    // Mock scheduler — prevents pool size errors in test context
    @MockBean private ReminderScheduler reminderScheduler;

    // Mock EmailService — prevents NPE from null MimeMessage on mock JavaMailSender
    @MockBean private EmailService emailService;

    private static String userToken;
    private static String adminToken;
    private static Long createdEventId;

    @BeforeEach
    void seedAdmin() {
        if (userRepository.findByEmail("admin@e2e.com").isEmpty()) {
            userRepository.save(User.builder()
                    .firstName("Admin").lastName("E2E")
                    .email("admin@e2e.com")
                    .password(passwordEncoder.encode("Admin1234"))
                    .role(Role.ROLE_ADMIN).enabled(true).build());
        }
    }

    @Test @Order(1)
    @DisplayName("E2E-1: New user can register")
    void step1_userRegistration() throws Exception {
        AuthRequest.Register req = new AuthRequest.Register();
        req.setFirstName("Bob"); req.setLastName("Test");
        req.setEmail("bob@e2e.com"); req.setPassword("Bob12345");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.email").value("bob@e2e.com"))
                .andReturn();

        userToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(userToken).isNotBlank();
    }

    @Test @Order(2)
    @DisplayName("E2E-2: Admin can login")
    void step2_adminLogin() throws Exception {
        AuthRequest.Login req = new AuthRequest.Login();
        req.setEmail("admin@e2e.com"); req.setPassword("Admin1234");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        adminToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(adminToken).isNotBlank();
    }

    @Test @Order(3)
    @DisplayName("E2E-3: Admin can create an event")
    void step3_adminCreatesEvent() throws Exception {
        EventRequest req = new EventRequest();
        req.setTitle("E2E Test Conference");
        req.setDescription("An end-to-end test event");
        req.setStartDateTime(LocalDateTime.now().plusDays(7));
        req.setEndDateTime(LocalDateTime.now().plusDays(7).plusHours(6));
        req.setLocation("Bangalore");
        req.setVenue("Convention Center");
        req.setCategory(EventCategory.CONFERENCE);
        req.setMaxCapacity(100);

        MvcResult result = mockMvc.perform(post("/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("E2E Test Conference"))
                .andExpect(jsonPath("$.availableSlots").value(100))
                .andReturn();

        createdEventId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
        assertThat(createdEventId).isPositive();
    }

    @Test @Order(4)
    @DisplayName("E2E-4: Any user can browse events without auth")
    void step4_browseEvents() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test @Order(5)
    @DisplayName("E2E-5: User can search events by keyword and category")
    void step5_searchEvents() throws Exception {
        mockMvc.perform(get("/events/search")
                        .param("search", "E2E")
                        .param("category", "CONFERENCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test @Order(6)
    @DisplayName("E2E-6: User can view event detail by ID")
    void step6_viewEventDetail() throws Exception {
        mockMvc.perform(get("/events/" + createdEventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdEventId))
                .andExpect(jsonPath("$.location").value("Bangalore"))
                .andExpect(jsonPath("$.speakers").isArray());
    }

    @Test @Order(7)
    @DisplayName("E2E-7: Authenticated user can register for an event")
    void step7_userRegistersForEvent() throws Exception {
        RegistrationRequest req = new RegistrationRequest();
        req.setEventId(createdEventId);
        req.setNotes("Looking forward to it!");

        mockMvc.perform(post("/registrations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REGISTERED"))
                .andExpect(jsonPath("$.eventId").value(createdEventId));
    }

    @Test @Order(8)
    @DisplayName("E2E-8: Duplicate registration is rejected with 409")
    void step8_duplicateRegistrationRejected() throws Exception {
        RegistrationRequest req = new RegistrationRequest();
        req.setEventId(createdEventId);

        mockMvc.perform(post("/registrations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test @Order(9)
    @DisplayName("E2E-9: User can view their own registrations")
    void step9_viewMyRegistrations() throws Exception {
        mockMvc.perform(get("/registrations/my")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test @Order(10)
    @DisplayName("E2E-10: Admin can view all registrations for an event")
    void step10_adminViewsEventRegistrations() throws Exception {
        mockMvc.perform(get("/admin/events/" + createdEventId + "/registrations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("REGISTERED"));
    }

    @Test @Order(11)
    @DisplayName("E2E-11: Admin can mark a registration as ATTENDED")
    void step11_adminUpdatesAttendance() throws Exception {
        Long regId = registrationRepository.findByEventId(createdEventId)
                .stream().findFirst().map(r -> r.getId()).orElseThrow();

        String body = "{\"registrationId\":" + regId + ",\"status\":\"ATTENDED\"}";

        mockMvc.perform(patch("/admin/attendance")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Attendance updated"));
    }

    @Test @Order(12)
    @DisplayName("E2E-12: User can cancel their registration")
    void step12_userCancelsRegistration() throws Exception {
        // Reset to REGISTERED first
        Long regId = registrationRepository.findByEventId(createdEventId)
                .stream().findFirst().map(r -> r.getId()).orElseThrow();
        mockMvc.perform(patch("/admin/attendance")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"registrationId\":" + regId + ",\"status\":\"REGISTERED\"}"));

        mockMvc.perform(delete("/registrations/events/" + createdEventId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration cancelled successfully"));
    }

    @Test @Order(13)
    @DisplayName("E2E-13: Unauthenticated access to protected route returns 401")
    void step13_unauthenticatedAccessBlocked() throws Exception {
        // Custom AuthenticationEntryPoint in SecurityConfig ensures 401 is returned
        // (not 403) for unauthenticated requests — correct REST API behaviour.
        mockMvc.perform(get("/registrations/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test @Order(14)
    @DisplayName("E2E-14: Regular user cannot access admin endpoints")
    void step14_regularUserBlockedFromAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test @Order(15)
    @DisplayName("E2E-15: Admin can view dashboard statistics")
    void step15_adminDashboardStats() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber())
                .andExpect(jsonPath("$.totalRegistrations").isNumber());
    }
}
