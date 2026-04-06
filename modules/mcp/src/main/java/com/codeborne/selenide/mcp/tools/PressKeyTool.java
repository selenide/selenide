package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.Keys;

import java.util.Map;

class PressKeyTool extends McpTool {
  PressKeyTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_press_key";
  }

  @Override
  String description() {
    return "Press a keyboard key on an element or the focused element";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "key": {
            "type": "string",
            "description": "Key to press: enter, escape, tab, or Keys enum name"
          },
          "selector": {
            "type": "string",
            "description": "Selector (optional; uses focused element if omitted)"
          }
        },
        "required": ["key"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String key = (String) args.get("key");
    String selector = (String) args.get("selector");
    var element = selector != null
      ? session.getDriver().$(resolve(selector))
      : session.getDriver().getFocusedElement();
    switch (key.toLowerCase()) {
      case "enter" -> element.pressEnter();
      case "escape" -> element.pressEscape();
      case "tab" -> element.pressTab();
      default -> {
        try {
          element.sendKeys(Keys.valueOf(key.toUpperCase()));
        }
        catch (IllegalArgumentException ignored) {
          element.sendKeys(key);
        }
      }
    }
    return success("Pressed key: " + key);
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    return selector != null ? selector : "(focused element)";
  }
}
