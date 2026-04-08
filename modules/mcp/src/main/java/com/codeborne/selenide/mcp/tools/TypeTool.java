package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class TypeTool extends McpTool {
  TypeTool(BrowserSession session) {
    super(session, "browser_type", "Type text into an element");
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
          "text": {
            "type": "string",
            "description": "Text to type into the element"
          },
          "submit": {
            "type": "boolean",
            "description": "Press Enter after typing"
          }
        },
        "required": ["selector", "text"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    String text = (String) args.get("text");
    var element = session.getDriver().$(resolve(selector)).type(text);
    if (Boolean.TRUE.equals(args.get("submit"))) {
      element.pressEnter();
    }
    return success("Typed '" + text + "' into " + selector);
  }
}
