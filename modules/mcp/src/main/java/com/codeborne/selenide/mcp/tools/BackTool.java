package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class BackTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{}}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  BackTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_back")
      .description("Navigate the browser back in history")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        try {
          session.getDriver().back();
          String currentUrl = session.getDriver().url();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Navigated back to: " + currentUrl)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "back"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
