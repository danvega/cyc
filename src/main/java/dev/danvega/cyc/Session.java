package dev.danvega.cyc;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        String track,
        String description
) {
}
