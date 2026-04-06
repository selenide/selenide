package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class NavigateTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{"url":{"type":"string","description":"The URL to navigate to"}},"required":["url"]}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  NavigateTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_navigate")
      .description("Navigate the browser to a URL")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String url = (String) request.arguments().get("url");
        try {
          session.getDriver().open(url);
          String currentUrl = session.getDriver().url();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Navigated to: " + currentUrl)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, url))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
