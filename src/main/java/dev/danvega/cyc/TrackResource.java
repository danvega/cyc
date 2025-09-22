package dev.danvega.cyc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TrackResource {

    private static final Logger log = LoggerFactory.getLogger(TrackResource.class);
    private Conference conference;
    private final ObjectMapper objectMapper;

    public TrackResource(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @McpResource(
            uri = "tracks://all",
            name = "Conference Tracks",
            description = "Provides a list of all tracks available at the Commit Your Code Conference"
    )
    public String getAllTracks() throws JsonProcessingException {
        return objectMapper.writeValueAsString(conference.tracks());
    }

    @PostConstruct
    public void init() {
        log.info("Loading Conference data for TrackResource from JSON file 'sessions.json'");
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/sessions.json")) {
            var jsonNode = objectMapper.readTree(inputStream);
            var conferenceNode = jsonNode.get("conference");
            this.conference = objectMapper.treeToValue(conferenceNode, Conference.class);
            log.info("Conference data loaded successfully: {} tracks available",
                    conference.tracks() != null ? conference.tracks().size() : 0);
        } catch (IOException e) {
            log.error("Failed to read JSON data for TrackResource", e);
            throw new RuntimeException("Failed to read JSON data for TrackResource", e);
        }
    }
}