package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class RefreshTool extends McpTool {
  RefreshTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_refresh";
  }

  @Override
  String description() {
    return "Refresh the current browser page";
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.getDriver().refresh();
    return success("Page refreshed");
  }
}
