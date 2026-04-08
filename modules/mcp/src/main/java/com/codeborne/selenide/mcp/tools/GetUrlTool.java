package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class GetUrlTool extends McpTool {
  GetUrlTool(BrowserSession session) {
    super(session, "browser_get_url", "Get the current URL of the browser");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    return success(session.getDriver().url());
  }
}
