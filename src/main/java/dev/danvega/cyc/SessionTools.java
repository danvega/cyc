package dev.danvega.cyc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class SessionTools {

    private static final Logger log = LoggerFactory.getLogger(SessionTools.class);
    private ConferenceData conferenceData;
    private final ObjectMapper objectMapper;

    public SessionTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @McpTool(description = "say hello")
    public String sayHello() {
        return "Hello";
    }

    @McpTool(description = "Get all conference data including sessions, rooms, and conference details")
    public ConferenceData getConferenceData() {
        return conferenceData;
    }

    @PostConstruct
    public void init() {
        log.info("Loading Sessions from JSON file 'sessions.json'");
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/sessions.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find sessions.json in classpath");
            }
            this.conferenceData = objectMapper.readValue(inputStream, ConferenceData.class);
            log.info("Sessions loaded successfully: {} sessions", 
                    conferenceData.sessions() != null ? conferenceData.sessions().size() : 0);
        } catch (IOException e) {
            log.error("Failed to read JSON data", e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

}
