package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.Keys;

import java.util.List;

class PressKeyTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "key": {
            "type": "string",
            "description": "Key to press: enter, escape, tab, or any Keys enum name"
          },
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector (optional; uses focused element if omitted)"
          }
        },
        "required": ["key"]
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  PressKeyTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_press_key")
      .description("Press a keyboard key on an element or the currently focused element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String key = (String) request.arguments().get("key");
        String selector = (String) request.arguments().get("selector");
        try {
          var element = selector != null
            ? session.getDriver().$(resolver.resolve(selector))
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
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Pressed key: " + key)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, selector != null ? selector : "(focused element)"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
