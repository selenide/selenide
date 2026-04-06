package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class RefreshTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{}}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  RefreshTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_refresh")
      .description("Refresh the current browser page")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        try {
          session.getDriver().refresh();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Page refreshed")))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "refresh"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
