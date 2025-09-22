package dev.danvega.cyc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SessionTools {

    private static final Logger log = LoggerFactory.getLogger(SessionTools.class);
    private Conference conference;
    private final ObjectMapper objectMapper;

    public SessionTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @McpTool(name = "cyc-get-conference-data", description = "Get all conference data including sessions, tracks, rooms and conference details")
    public Conference getConferenceData() {
        return conference;
    }

    @McpTool(name = "cyc-sessions-by-date", description = "Returns the count of sessions by date")
    public String countSessionsByDate() throws JsonProcessingException {
        Map<String, Long> sessionsByDate = conference.sessions().stream()
                .collect(Collectors.groupingBy(
                        Session::day,
                        Collectors.counting()
                ));
        return objectMapper.writeValueAsString(sessionsByDate);
    }

    @McpTool(name = "cyc-sessions-by-track", description = "Returns the count of sessions for a specific track")
    public String countSessionsByTrack(@McpToolParam String track) throws JsonProcessingException {
        long sessionCount = conference.sessions().stream()
                .filter(session -> session.track() != null && session.track().contains(track))
                .count();

        Map<String, Object> result = Map.of("track", track, "count", sessionCount);
        return objectMapper.writeValueAsString(result);
    }

    @PostConstruct
    public void init() {
        log.info("Loading Sessions from JSON file 'sessions.json'");
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/sessions.json")) {
            var jsonNode = objectMapper.readTree(inputStream);
            var conferenceNode = jsonNode.get("conference");
            this.conference = objectMapper.treeToValue(conferenceNode, Conference.class);
            log.info("Sessions loaded successfully: {} sessions",
                    conference.sessions() != null ? conference.sessions().size() : 0);
        } catch (IOException e) {
            log.error("Failed to read JSON data", e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

}
