package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class ClickTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector"
          },
          "doubleClick": {
            "type": "boolean",
            "description": "Perform a double click"
          },
          "contextClick": {
            "type": "boolean",
            "description": "Perform a right-click (context click)"
          }
        },
        "required": ["selector"]
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ClickTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_click")
      .description("Click an element on the page")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        Boolean doubleClick = (Boolean) request.arguments().get("doubleClick");
        Boolean contextClick = (Boolean) request.arguments().get("contextClick");
        try {
          var by = resolver.resolve(selector);
          if (Boolean.TRUE.equals(doubleClick)) {
            session.getDriver().$(by).doubleClick();
          }
          else if (Boolean.TRUE.equals(contextClick)) {
            session.getDriver().$(by).contextClick();
          }
          else {
            session.getDriver().$(by).click();
          }
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Clicked: " + selector)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
