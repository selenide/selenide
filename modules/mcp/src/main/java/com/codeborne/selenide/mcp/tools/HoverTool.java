package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class HoverTool extends McpTool {
  HoverTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_hover";
  }

  @Override
  String description() {
    return "Hover over an element";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector"
          }
        },
        "required": ["selector"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    session.getDriver().$(resolve(selector)).hover();
    return success("Hovered: " + selector);
  }
}
