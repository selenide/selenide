package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FrameResetTool extends McpTool {
  FrameResetTool(BrowserSession session) {
    super(session, "browser_frame_reset", "Return the browser context to the top-level document");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.getDriver().switchTo().defaultContent();
    return success("Switched to default content");
  }
}
