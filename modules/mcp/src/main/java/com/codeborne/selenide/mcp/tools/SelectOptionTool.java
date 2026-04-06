package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class SelectOptionTool extends McpTool {
  SelectOptionTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_select_option";
  }

  @Override
  String description() {
    return "Select an option in a <select> element by text, value, or index";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Selector for the select element"
          },
          "text": {
            "type": "string",
            "description": "Option text to select"
          },
          "value": {
            "type": "string",
            "description": "Option value to select"
          },
          "index": {
            "type": "integer",
            "description": "Option index to select"
          }
        },
        "required": ["selector"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    String text = (String) args.get("text");
    String value = (String) args.get("value");
    Number index = (Number) args.get("index");

    if (text == null && value == null && index == null) {
      throw new IllegalArgumentException(
        "One of 'text', 'value', or 'index' is required");
    }

    var element = session.getDriver().$(resolve(selector));
    if (text != null) {
      element.selectOption(text);
    }
    else if (value != null) {
      element.selectOptionByValue(value);
    }
    else {
      element.selectOption(index.intValue());
    }
    String selectedText = element.getSelectedOptionText();
    return success("Selected '" + selectedText + "' in " + selector);
  }
}
