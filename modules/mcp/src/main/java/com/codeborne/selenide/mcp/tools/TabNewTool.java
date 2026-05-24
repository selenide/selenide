package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Map;

class TabNewTool extends McpTool {
  TabNewTool(BrowserSession session) {
    super(session, "browser_tab_new",
      "Open a new browser tab; optionally navigate to a URL");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "url": {"type": "string", "description": "Optional URL to open in the new tab"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String url = (String) args.get("url");
    WebDriver driver = session.getDriver().getWebDriver();
    driver.switchTo().newWindow(WindowType.TAB);
    if (url != null && !url.isEmpty()) {
      session.getDriver().open(url);
    }
    return success("Opened new tab" + (url != null ? " at " + url : ""));
  }
}
