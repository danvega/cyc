package dev.danvega.cyc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpPrompt;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConferencePrompts {

    private static final Logger log = LoggerFactory.getLogger(ConferencePrompts.class);

    @McpPrompt(
        name = "cyc-sessions-per-day",
        description = "Analyze Commit your Code (CYC) 2025 conference sessions and get a breakdown by day plus total session count")
    public GetPromptResult sessionsPerDayPrompt() {
        String message = "As a conference data analyst, please analyze the Commit your Code (CYC) 2025 conference sessions and tell me:\n\n" +
                "1. How many sessions are scheduled for each day of the conference?\n" +
                "2. What is the total number of sessions across all days?\n" +
                "3. Provide a summary showing the distribution and any insights about the schedule.\n\n" +
                "Use the cyc-sessions-by-date tool to get the session counts by date. This tool returns a map with dates as keys and session counts as values. " +
                "Calculate the total by adding up all the daily counts.";

        return new GetPromptResult(
            "CYC 2025 sessions analysis by day",
            List.of(new PromptMessage(Role.USER, new TextContent(message)))
        );
    }
}