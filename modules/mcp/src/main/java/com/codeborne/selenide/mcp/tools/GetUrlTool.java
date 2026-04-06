package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class GetUrlTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{}}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  GetUrlTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_get_url")
      .description("Get the current URL of the browser")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        try {
          String currentUrl = session.getDriver().getWebDriver().getCurrentUrl();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(currentUrl)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "get_url"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
