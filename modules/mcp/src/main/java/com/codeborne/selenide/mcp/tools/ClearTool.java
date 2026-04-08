package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class ClearTool extends McpTool {
  ClearTool(BrowserSession session) {
    super(session, "browser_clear", "Clear the value of an input element");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    session.getDriver().$(resolve(selector)).clear();
    return success("Cleared: " + selector);
  }
}
