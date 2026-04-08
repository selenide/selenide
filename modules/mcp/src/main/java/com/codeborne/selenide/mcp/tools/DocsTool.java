package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


class DocsTool extends McpTool {
  private static final List<String> TOPICS = List.of(
    "selectors", "conditions", "commands", "collections",
    "page_object", "configuration", "file_download"
  );

  DocsTool(BrowserSession session) {
    super(session, "selenide_docs",
      "Get Selenide API documentation and code examples. "
        + "Available topics: " + String.join(", ", TOPICS));
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "topic": {
            "type": "string",
            "enum": ["selectors", "conditions", "commands", "collections",
                     "page_object", "configuration", "file_download"],
            "description": "Documentation topic"
          }
        },
        "required": ["topic"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String topic = (String) args.get("topic");
    if (!TOPICS.contains(topic)) {
      return success("Unknown topic: " + topic
        + ". Available: " + String.join(", ", TOPICS));
    }
    return success(loadDoc(topic));
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return String.valueOf(args.get("topic"));
  }

  private String loadDoc(String topic) {
    String resource = "/com/codeborne/selenide/mcp/docs/" + topic + ".md";
    InputStream is = DocsTool.class.getResourceAsStream(resource);
    if (is == null) {
      return "Documentation not found for topic: " + topic;
    }
    try (is) {
      return new String(is.readAllBytes(), UTF_8);
    }
    catch (IOException e) {
      return "Failed to load documentation: " + e;
    }
  }
}
