package dev.danvega.cyc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(dev.danvega.cyc.SessionTools.class);
    private Conference conference;
    private final ObjectMapper objectMapper;

    public SessionController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/conference")
    public Conference getConferenceData() {
        return conference;
    }

    @GetMapping("/sessions")
    public List<Session> getSessions() {
        return conference.sessions();
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
