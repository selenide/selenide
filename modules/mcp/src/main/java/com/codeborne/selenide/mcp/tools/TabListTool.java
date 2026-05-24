package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.Map;

class TabListTool extends McpTool {
  TabListTool(BrowserSession session) {
    super(session, "browser_tab_list",
      "List all open browser tabs with index, handle, title and url");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    WebDriver driver = session.getDriver().getWebDriver();
    String activeHandle = driver.getWindowHandle();
    StringBuilder out = new StringBuilder();
    int i = 0;
    for (String handle : driver.getWindowHandles()) {
      driver.switchTo().window(handle);
      out.append('[').append(i++).append("] handle=\"").append(handle).append('"')
        .append(" title=\"").append(driver.getTitle()).append('"')
        .append(" url=\"").append(driver.getCurrentUrl()).append('"');
      if (handle.equals(activeHandle)) {
        out.append(" (active)");
      }
      out.append('\n');
    }
    driver.switchTo().window(activeHandle);
    return success(out.toString().trim());
  }
}
