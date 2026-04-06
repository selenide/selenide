package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class HandleDialogTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{"accept":{"type":"boolean","description":"True to accept/confirm the dialog, false to dismiss/cancel it"}},"required":["accept"]}
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  HandleDialogTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_handle_dialog")
      .description("Accept or dismiss a browser dialog (alert, confirm, prompt)")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        Boolean accept = (Boolean) request.arguments().get("accept");
        try {
          if (Boolean.TRUE.equals(accept)) {
            session.getDriver().modal().confirm();
            return McpSchema.CallToolResult.builder()
              .content(List.of(new McpSchema.TextContent("Dialog accepted")))
              .isError(false)
              .build();
          }
          else {
            session.getDriver().modal().dismiss();
            return McpSchema.CallToolResult.builder()
              .content(List.of(new McpSchema.TextContent("Dialog dismissed")))
              .isError(false)
              .build();
          }
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "(dialog)"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
