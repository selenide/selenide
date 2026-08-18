package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FrameSelectTool extends McpTool {
  FrameSelectTool(BrowserSession session) {
    super(session, "browser_frame_select", "Switch the browser context into an iframe located by selector");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    var by = resolve(selector);
    session.getDriver().switchTo().frame(session.getDriver().$(by));
    return success("Switched into frame: " + selector);
  }
}
