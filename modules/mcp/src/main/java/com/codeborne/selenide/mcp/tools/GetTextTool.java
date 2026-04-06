package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class GetTextTool extends McpTool {
  GetTextTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_get_text";
  }

  @Override
  String description() {
    return "Get the visible text content of an element";
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
    return success(session.getDriver().$(resolve(selector)).getText());
  }
}
