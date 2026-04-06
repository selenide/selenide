package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class ConsoleLogsTool extends McpTool {
  ConsoleLogsTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_console_logs";
  }

  @Override
  String description() {
    return "Retrieve browser console log entries (Chromium-based browsers only)";
  }

  @Override
  String inputSchema() {
    return """
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
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String levelStr = (String) args.getOrDefault("level", "ALL");
    Level minLevel = "ALL".equals(levelStr) ? Level.ALL : Level.parse(levelStr);
    List<LogEntry> entries = session.getDriver().getWebDriver()
      .manage().logs().get(LogType.BROWSER).getAll();
    List<LogEntry> filtered = entries.stream()
      .filter(e -> e.getLevel().intValue() >= minLevel.intValue())
      .toList();
    if (filtered.isEmpty()) {
      return success("No console logs");
    }
    StringBuilder sb = new StringBuilder();
    for (LogEntry entry : filtered) {
      sb.append("[").append(entry.getLevel().getName()).append("] ")
        .append(entry.getMessage()).append("\n");
    }
    return success(sb.toString().stripTrailing());
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return "(console logs)";
  }
}
