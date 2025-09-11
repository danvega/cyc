package dev.danvega.cyc;

import java.util.List;

public record Conference(
        String name,
        int year,
        String[] dates,
        String location,
        List<String> tracks,
        List<String> rooms,
        List<Session> sessions
) {
}
