package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class ClearTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector"
          }
        },
        "required": ["selector"]
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ClearTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_clear")
      .description("Clear the value of an input element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          session.getDriver().$(by).clear();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Cleared: " + selector)))
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
