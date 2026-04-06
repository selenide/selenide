package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.List;
import java.util.logging.Level;

class ConsoleLogsTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "level": {
            "type": "string",
            "enum": ["ALL", "SEVERE", "WARNING", "INFO"],
            "description": "Minimum log level to return (default ALL)"
          }
        }
      }
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ConsoleLogsTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_console_logs")
      .description("Retrieve browser console log entries")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String levelStr = (String) request.arguments().getOrDefault("level", "ALL");
        try {
          Level minLevel = levelStr.equals("ALL") ? Level.ALL : Level.parse(levelStr);
          List<LogEntry> entries = session.getDriver().getWebDriver()
            .manage().logs().get(LogType.BROWSER).getAll();
          List<LogEntry> filtered = entries.stream()
            .filter(e -> e.getLevel().intValue() >= minLevel.intValue())
            .toList();
          if (filtered.isEmpty()) {
            return McpSchema.CallToolResult.builder()
              .content(List.of(new McpSchema.TextContent("No console logs")))
              .isError(false)
              .build();
          }
          StringBuilder sb = new StringBuilder();
          for (LogEntry entry : filtered) {
            sb.append("[").append(entry.getLevel().getName()).append("] ")
              .append(entry.getMessage()).append("\n");
          }
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(sb.toString().stripTrailing())))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "(console logs)"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
