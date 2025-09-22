package dev.danvega.cyc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrackResourceTest {

    @Autowired
    private TrackResource trackResource;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTracks() throws Exception {
        String tracksJson = trackResource.getAllTracks();
        assertNotNull(tracksJson);

        String[] tracks = objectMapper.readValue(tracksJson, String[].class);

        assertNotNull(tracks);
        assertTrue(tracks.length > 0);

        assertEquals(6, tracks.length);
        assertTrue(java.util.Arrays.asList(tracks).contains("JavaScript"));
        assertTrue(java.util.Arrays.asList(tracks).contains("Java"));
        assertTrue(java.util.Arrays.asList(tracks).contains("Cloud"));
        assertTrue(java.util.Arrays.asList(tracks).contains(".NET"));
        assertTrue(java.util.Arrays.asList(tracks).contains("Leadership"));
        assertTrue(java.util.Arrays.asList(tracks).contains("AI"));
    }
}