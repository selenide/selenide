package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class CloseTool extends McpTool {
  CloseTool(BrowserSession session) {
    super(session, "browser_close", "Close the browser");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.close();
    return success("Browser closed");
  }
}
