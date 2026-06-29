package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

class TabSelectTool extends McpTool {
  TabSelectTool(BrowserSession session) {
    super(session, "browser_tab_select",
      "Switch to a tab by 0-based index or by window handle");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "index":  {"type": "integer", "description": "0-based tab index"},
          "handle": {"type": "string",  "description": "Window handle string"}
        },
        "minProperties": 1
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    Number indexRaw = (Number) args.get("index");
    String handle = (String) args.get("handle");
    if (indexRaw == null && handle == null) {
      throw new IllegalArgumentException("Provide either 'index' or 'handle'");
    }
    if (indexRaw != null && handle != null) {
      throw new IllegalArgumentException("Provide only one of 'index' or 'handle'");
    }
    WebDriver driver = session.getDriver().getWebDriver();
    String target;
    if (indexRaw != null) {
      List<String> handles = List.copyOf(driver.getWindowHandles());
      int index = indexRaw.intValue();
      if (index < 0 || index >= handles.size()) {
        throw new IllegalArgumentException(
          "Tab index " + index + " out of range (0.." + (handles.size() - 1) + ")");
      }
      target = handles.get(index);
    } else {
      target = handle;
    }
    driver.switchTo().window(target);
    return success("Switched to tab: " + target);
  }
}
