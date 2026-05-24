package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.Set;

class TabCloseTool extends McpTool {
  TabCloseTool(BrowserSession session) {
    super(session, "browser_tab_close",
      "Close a browser tab. With no args, closes the current tab");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "index":  {"type": "integer", "description": "0-based tab index"},
          "handle": {"type": "string",  "description": "Window handle string"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    WebDriver driver = session.getDriver().getWebDriver();
    Number indexRaw = (Number) args.get("index");
    String handle = (String) args.get("handle");
    String target;
    if (indexRaw == null && handle == null) {
      target = driver.getWindowHandle();
    } else if (indexRaw != null && handle != null) {
      throw new IllegalArgumentException("Provide only one of 'index' or 'handle'");
    } else if (indexRaw != null) {
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
    String active = driver.getWindowHandle();
    driver.switchTo().window(target);
    driver.close();
    Set<String> remaining = driver.getWindowHandles();
    if (remaining.isEmpty()) {
      return success("Closed last tab; no remaining tabs");
    }
    String next = target.equals(active) ? remaining.iterator().next() : active;
    driver.switchTo().window(next);
    return success("Closed tab: " + target);
  }
}
