package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class ForwardTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{}}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ForwardTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_forward")
      .description("Navigate the browser forward in history")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        try {
          session.getDriver().forward();
          String currentUrl = session.getDriver().url();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Navigated forward to: " + currentUrl)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "forward"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
