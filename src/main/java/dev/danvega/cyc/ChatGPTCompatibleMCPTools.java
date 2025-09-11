package dev.danvega.cyc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatGPTCompatibleMCPTools {

    private static final Logger log = LoggerFactory.getLogger(ChatGPTCompatibleMCPTools.class);
    private Conference conference;
    private final ObjectMapper objectMapper;

    public ChatGPTCompatibleMCPTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @McpTool(
        name = "search", 
        description = "Search conference sessions by query. Use this to find sessions matching specific criteria like speaker names, topics, tracks, or keywords."
    )
    public SearchResult search(
        String query,
        Integer limit
    ) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchResult("Query parameter is required", new ArrayList<>());
        }

        String lowerQuery = query.toLowerCase();
        int maxResults = limit != null ? Math.max(1, Math.min(limit, 50)) : 10;

        List<SessionSearchResult> matchingSessions = conference.sessions().stream()
            .filter(session -> matchesQuery(session, lowerQuery))
            .limit(maxResults)
            .map(session -> new SessionSearchResult(
                generateSessionId(session),
                session.title(),
                session.speakers() != null ? String.join(", ", session.speakers()) : "",
                session.track() != null ? String.join(", ", session.track()) : "",
                session.day() + " " + session.time(),
                session.room(),
                truncateDescription(session.description(), 150)
            ))
            .collect(Collectors.toList());

        String resultMessage = String.format("Found %d sessions matching '%s'", 
            matchingSessions.size(), query);

        return new SearchResult(resultMessage, matchingSessions);
    }

    @McpTool(
        name = "fetch", 
        description = "Fetch detailed information for a specific session using its ID. Use this after searching to get complete session details."
    )
    public FetchResult fetch(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return new FetchResult("Session ID is required", null);
        }

        Session session = findSessionById(sessionId);
        if (session == null) {
            return new FetchResult("Session not found with ID: " + sessionId, null);
        }

        SessionDetail details = new SessionDetail(
            sessionId,
            session.title(),
            session.speakers() != null ? List.of(session.speakers()) : new ArrayList<>(),
            session.speakerCompanies() != null ? List.of(session.speakerCompanies()) : new ArrayList<>(),
            session.track() != null ? session.track() : new ArrayList<>(),
            session.day(),
            session.time(),
            session.duration(),
            session.room(),
            session.type(),
            session.description(),
            conference.name(),
            conference.year(),
            conference.location()
        );

        return new FetchResult("Session details retrieved successfully", details);
    }

    private boolean matchesQuery(Session session, String query) {
        // Search in title
        if (session.title() != null && session.title().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in speakers
        if (session.speakers() != null) {
            for (String speaker : session.speakers()) {
                if (speaker != null && speaker.toLowerCase().contains(query)) {
                    return true;
                }
            }
        }
        
        // Search in tracks
        if (session.track() != null) {
            for (String track : session.track()) {
                if (track != null && track.toLowerCase().equals(query)) {
                    return true;
                }
            }
        }
        
        // Search in description
        if (session.description() != null && session.description().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in room
        if (session.room() != null && session.room().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in type
        if (session.type() != null && session.type().toLowerCase().contains(query)) {
            return true;
        }
        
        return false;
    }

    private String generateSessionId(Session session) {
        // Generate a simple ID based on title and time
        String baseId = session.title().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String timeId = (session.day() + session.time()).replaceAll("[^a-zA-Z0-9]", "");
        return baseId.substring(0, Math.min(baseId.length(), 20)) + "_" + timeId;
    }

    private Session findSessionById(String sessionId) {
        return conference.sessions().stream()
            .filter(session -> generateSessionId(session).equals(sessionId))
            .findFirst()
            .orElse(null);
    }

    private String truncateDescription(String description, int maxLength) {
        if (description == null) return "";
        if (description.length() <= maxLength) return description;
        return description.substring(0, maxLength) + "...";
    }

    @PostConstruct
    public void init() {
        log.info("Loading Conference data for ChatGPT-compatible MCP tools");
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/sessions.json")) {
            var jsonNode = objectMapper.readTree(inputStream);
            var conferenceNode = jsonNode.get("conference");
            this.conference = objectMapper.treeToValue(conferenceNode, Conference.class);
            log.info("Conference data loaded successfully for ChatGPT MCP: {} sessions",
                    conference.sessions() != null ? conference.sessions().size() : 0);
        } catch (IOException e) {
            log.error("Failed to read JSON data for ChatGPT MCP tools", e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

    // Result classes for proper JSON serialization
    public record SearchResult(String message, List<SessionSearchResult> sessions) {}
    
    public record SessionSearchResult(
        String id,
        String title, 
        String speakers,
        String track,
        String schedule,
        String room,
        String preview
    ) {}
    
    public record FetchResult(String message, SessionDetail session) {}
    
    public record SessionDetail(
        String id,
        String title,
        List<String> speakers,
        List<String> speakerCompanies,
        List<String> tracks,
        String day,
        String time,
        String duration,
        String room,
        String type,
        String description,
        String conferenceName,
        int conferenceYear,
        String conferenceLocation
    ) {}
}