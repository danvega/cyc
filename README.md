# Building MCP Servers with Java & Spring Boot

**Conference Talk Demo for Commit your Code 2025**

This repository demonstrates how to build Model Context Protocol (MCP) servers using Java and Spring Boot. Created for the "Building MCP Servers in Java/Spring" presentation at Commit your Code 2025.

## What is MCP?

The Model Context Protocol (MCP) enables AI applications to securely connect to data sources and tools. It provides a standardized way to expose functionality to AI models through:

- **Tools**: Functions that AI models can call
- **Prompts**: Reusable prompt templates
- **Resources**: Access to data sources

## Demo Features

This MCP server showcases conference session data analysis with:

- `@McpTool` annotations for exposing data analysis functions
- `@McpPrompt` annotations for reusable AI interactions
- Conference session querying by date and track
- Both STDIO and HTTP transport protocols
- Spring Boot auto-configuration

## Quick Start

### Build and Run

```bash
# Build the project
mvn clean package

# Run the MCP server
mvn spring-boot:run
```

### Configure with Claude Desktop

Add to your Claude Desktop MCP settings (`claude_desktop_config.json`):

```json
{
  "mcpServers": {
    "cyc": {
      "command": "java",
      "args": [
        "-jar",
        "target/cyc-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Try It Out

Once configured, you can ask Claude to:

- "Get the conference data using the cyc tools"
- "How many sessions are scheduled each day?"
- "Show me sessions for the Java track"
- "Use the cyc-sessions-per-day prompt to analyze the schedule"

## Tech Stack

- **Spring Boot 3.5.5** - Application framework
- **Spring AI 1.1.0-M1** - MCP server support
- **Java 17** - Runtime
- **Maven** - Build tool

## Key MCP Concepts Demonstrated

### Tools (`@McpTool`)
```java
@McpTool(name = "cyc-get-conference-data",
         description = "Get all conference data")
public Conference getConferenceData() {
    return conference;
}
```

### Prompts (`@McpPrompt`)
```java
@McpPrompt(name = "cyc-sessions-per-day",
           description = "Analyze conference sessions by day")
public GetPromptResult sessionsPerDayPrompt() {
    // Prompt implementation
}
```

---

*Learn more about building MCP servers at Commit your Code 2025!*