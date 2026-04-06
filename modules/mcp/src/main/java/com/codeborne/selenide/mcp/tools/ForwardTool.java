package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class ForwardTool extends McpTool {
  ForwardTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_forward";
  }

  @Override
  String description() {
    return "Navigate the browser forward in history";
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.getDriver().forward();
    return success("Navigated forward to: " + session.getDriver().url());
  }
}
