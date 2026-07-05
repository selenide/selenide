package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

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
    String target = indexRaw == null && handle == null
      ? driver.getWindowHandle()
      : resolveWindowHandle(driver, indexRaw, handle);
    String active = driver.getWindowHandle();
    // Closing the last open window typically ends the WebDriver session, so getWindowHandles()
    // afterwards would throw; decide upfront instead of probing the driver after close().
    boolean closingLastTab = driver.getWindowHandles().size() == 1;
    driver.switchTo().window(target);
    driver.close();
    if (closingLastTab) {
      return success("Closed last tab; no remaining tabs");
    }
    Set<String> remaining = driver.getWindowHandles();
    String next = remaining.contains(active) ? active : remaining.iterator().next();
    driver.switchTo().window(next);
    return success("Closed tab: " + target);
  }
}
