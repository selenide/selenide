package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class ClickTool extends McpTool {
  ClickTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_click";
  }

  @Override
  String description() {
    return "Click an element on the page";
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
          },
          "doubleClick": {
            "type": "boolean",
            "description": "Perform a double click"
          },
          "contextClick": {
            "type": "boolean",
            "description": "Perform a right-click (context click)"
          }
        },
        "required": ["selector"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    var by = resolve(selector);
    if (Boolean.TRUE.equals(args.get("doubleClick"))) {
      session.getDriver().$(by).doubleClick();
    }
    else if (Boolean.TRUE.equals(args.get("contextClick"))) {
      session.getDriver().$(by).contextClick();
    }
    else {
      session.getDriver().$(by).click();
    }
    return success("Clicked: " + selector);
  }
}
