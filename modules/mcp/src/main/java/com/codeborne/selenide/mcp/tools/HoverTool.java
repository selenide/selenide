package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class HoverTool extends McpTool {
  HoverTool(BrowserSession session) {
    super(session, "browser_hover", "Hover over an element");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    session.getDriver().$(resolve(selector)).hover();
    return success("Hovered: " + selector);
  }
}
