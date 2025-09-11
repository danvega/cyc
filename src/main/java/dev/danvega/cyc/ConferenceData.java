package dev.danvega.cyc;

import java.util.List;

public record ConferenceData(
        Conference conference,
        List<String> rooms,
        List<Session> sessions
) {
}