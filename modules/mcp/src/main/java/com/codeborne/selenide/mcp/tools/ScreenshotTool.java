package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.Base64;
import java.util.List;

class ScreenshotTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector for a specific element (optional; captures full page if omitted)"
          }
        }
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ScreenshotTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_screenshot")
      .description("Take a screenshot of the entire page or a specific element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          byte[] bytes;
          if (selector != null) {
            var by = resolver.resolve(selector);
            bytes = session.getDriver().$(by).getScreenshotAs(OutputType.BYTES);
          }
          else {
            bytes = ((TakesScreenshot) session.getDriver().getWebDriver()).getScreenshotAs(OutputType.BYTES);
          }
          String base64String = Base64.getEncoder().encodeToString(bytes);
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("data:image/png;base64," + base64String)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, selector != null ? selector : "(page)"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
