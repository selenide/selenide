package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class AppendTool extends McpTool {
  AppendTool(BrowserSession session) {
    super(session, "browser_append", "Append text to an element without clearing its current value first");
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
            "description": "Text to append to the element's current value"
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
    session.getDriver().$(resolve(selector)).append(text);
    return success("Appended '" + text + "' to " + selector);
  }
}
