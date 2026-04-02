package com.eventmanagement.repository;

import com.eventmanagement.entity.Event;
import com.eventmanagement.enums.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
// 1. Force replacement of any existing database configuration with an embedded one
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        // 2. Force table creation
        "spring.jpa.hibernate.ddl-auto=create-drop",
        // 3. Explicitly set the dialect using the properties structure Spring Boot 3 prefers
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        // 4. Use a pure H2 memory URL. CRITICAL: Do NOT use MODE=MySQL here.
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@DisplayName("EventRepository Tests")
class EventRepositoryTest {

    @Autowired private EventRepository eventRepository;
    @Autowired private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persistAndFlush(Event.builder()
                .title("Spring Boot Workshop")
                .startDateTime(LocalDateTime.now().plusDays(2))
                .endDateTime(LocalDateTime.now().plusDays(2).plusHours(4))
                .location("Bangalore").category(EventCategory.WORKSHOP)
                .maxCapacity(50).isActive(true).build());

        em.persistAndFlush(Event.builder()
                .title("AI Conference")
                .startDateTime(LocalDateTime.now().plusDays(10))
                .endDateTime(LocalDateTime.now().plusDays(10).plusHours(8))
                .location("Mumbai").category(EventCategory.CONFERENCE)
                .maxCapacity(300).isActive(true).build());

        em.persistAndFlush(Event.builder()
                .title("Inactive Seminar")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .location("Delhi").category(EventCategory.SEMINAR)
                .maxCapacity(100).isActive(false).build());
    }

    @Test
    @DisplayName("findByIsActiveTrue returns only active events")
    void findByIsActiveTrue_returnsOnlyActive() {
        Page<Event> result = eventRepository.findByIsActiveTrue(PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(Event::getIsActive);
    }

    @Test
    @DisplayName("searchEvents by title keyword")
    void searchEvents_byTitle() {
        Page<Event> result = eventRepository.searchEvents(
                "spring", null, null, null, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("spring");
    }

    @Test
    @DisplayName("searchEvents by category")
    void searchEvents_byCategory() {
        Page<Event> result = eventRepository.searchEvents(
                null, EventCategory.CONFERENCE, null, null, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(EventCategory.CONFERENCE);
    }

    @Test
    @DisplayName("findUpcomingEventsInRange returns events in window")
    void findUpcomingEventsInRange() {
        List<Event> events = eventRepository.findUpcomingEventsInRange(
                LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Spring Boot Workshop");
    }

    @Test
    @DisplayName("countActiveEvents returns correct count")
    void countActiveEvents() {
        long count = eventRepository.countActiveEvents();
        assertThat(count).isEqualTo(2);
    }
}