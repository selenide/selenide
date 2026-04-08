package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class GetTextTool extends McpTool {
  GetTextTool(BrowserSession session) {
    super(session, "browser_get_text",
      "Get the visible text content of an element");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    return success(session.getDriver().$(resolve(selector)).getText());
  }
}
