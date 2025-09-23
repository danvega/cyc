package dev.danvega.cyc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SessionToolsTest {

    @Autowired
    private SessionTools sessionTools;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConferenceDataReturnsValidData() {
        Conference conference = sessionTools.getConferenceData();

        assertThat(conference).isNotNull();
        assertThat(conference.name()).isEqualTo("Commit Your Code Conference (CYC25)");
        assertThat(conference.year()).isEqualTo(2025);
        assertThat(conference.dates()).containsExactly("2025-09-25", "2025-09-26");
        assertThat(conference.location()).isEqualTo("Yum! Brands International, Plano, TX");

        assertThat(conference.tracks()).isNotEmpty();
        assertThat(conference.tracks()).contains("JavaScript", "Java", "Cloud", ".NET", "Leadership", "AI");

        assertThat(conference.rooms()).isNotEmpty();
        assertThat(conference.rooms()).contains("Red Room", "Yellow Room");

        assertThat(conference.sessions()).isNotEmpty();

        Session firstSession = conference.sessions().get(0);
        assertThat(firstSession).isNotNull();
        assertThat(firstSession.title()).isNotBlank();
        assertThat(firstSession.day()).isNotBlank();
        assertThat(firstSession.time()).isNotBlank();
        assertThat(firstSession.speakers()).isNotNull();

        boolean hasSessionWithSpeakers = conference.sessions().stream()
                .anyMatch(session -> session.speakers() != null && session.speakers().length > 0);
        assertThat(hasSessionWithSpeakers).isTrue();
    }

    @Test
    void countSessionsByDateReturnsCorrectCounts() {
        Map<String, Long> sessionsByDate = sessionTools.countSessionsByDate();

        assertThat(sessionsByDate).isNotNull();
        assertThat(sessionsByDate).hasSize(2);

        assertThat(sessionsByDate).containsEntry("2025-09-25", 64L);
        assertThat(sessionsByDate).containsEntry("2025-09-26", 64L);

        long totalSessions = sessionsByDate.values().stream().mapToLong(Long::longValue).sum();
        assertThat(totalSessions).isEqualTo(128L);
    }

    @Test
    void countSessionsByTrackReturnsCorrectCount() {
        Map<String, Object> sessionsByTrack = sessionTools.countSessionsByTrack("Java");

        assertThat(sessionsByTrack).isNotNull();
        assertThat(sessionsByTrack).containsEntry("track", "Java");
        assertThat(sessionsByTrack).containsKey("count");
        assertThat(sessionsByTrack.get("count")).isInstanceOf(Number.class);

        Number count = (Number) sessionsByTrack.get("count");
        assertThat(count.longValue()).isEqualTo(17);
    }
}