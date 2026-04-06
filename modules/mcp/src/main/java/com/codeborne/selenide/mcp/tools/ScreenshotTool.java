package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.Base64;
import java.util.Map;

class ScreenshotTool extends McpTool {
  ScreenshotTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_screenshot";
  }

  @Override
  String description() {
    return "Take a screenshot of the entire page or a specific element";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Selector for a specific element (optional; full page if omitted)"
          }
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    byte[] bytes;
    if (selector != null) {
      bytes = session.getDriver().$(resolve(selector))
        .getScreenshotAs(OutputType.BYTES);
    }
    else {
      bytes = ((TakesScreenshot) session.getDriver().getWebDriver())
        .getScreenshotAs(OutputType.BYTES);
    }
    String base64 = Base64.getEncoder().encodeToString(bytes);
    return successImage(base64, "image/png");
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    return selector != null ? selector : "(page)";
  }
}
