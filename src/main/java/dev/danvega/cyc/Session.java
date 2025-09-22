package dev.danvega.cyc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Session(
        String day,
        String time,
        String duration,
        String title,
        String type,
        String[] speakers,
        @JsonProperty("speaker_companies")
        String[] speakerCompanies,
        String room,
        List<String> track
) {
}
