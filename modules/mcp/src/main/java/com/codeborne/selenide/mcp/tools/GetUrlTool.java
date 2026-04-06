package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class GetUrlTool extends McpTool {
  GetUrlTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_get_url";
  }

  @Override
  String description() {
    return "Get the current URL of the browser";
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
