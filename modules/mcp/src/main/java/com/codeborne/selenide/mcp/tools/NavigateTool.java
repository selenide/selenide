package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class NavigateTool extends McpTool {
  NavigateTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_navigate";
  }

  @Override
  String description() {
    return "Navigate the browser to a URL";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "url": {
            "type": "string",
            "description": "The URL to navigate to"
          }
        },
        "required": ["url"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String url = (String) args.get("url");
    session.getDriver().open(url);
    return success("Navigated to: " + session.getDriver().url());
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return (String) args.get("url");
  }
}
