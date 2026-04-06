package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class BackTool extends McpTool {
  BackTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_back";
  }

  @Override
  String description() {
    return "Navigate the browser back in history";
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.getDriver().back();
    return success("Navigated back to: " + session.getDriver().url());
  }
}
