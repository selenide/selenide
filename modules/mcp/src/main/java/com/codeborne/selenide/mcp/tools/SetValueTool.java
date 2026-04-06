package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class SetValueTool extends McpTool {
  SetValueTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_set_value";
  }

  @Override
  String description() {
    return "Set the value of an input element";
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
          "value": {
            "type": "string",
            "description": "Value to set on the element"
          }
        },
        "required": ["selector", "value"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    String value = (String) args.get("value");
    session.getDriver().$(resolve(selector)).setValue(value);
    return success("Set value '" + value + "' on " + selector);
  }
}
