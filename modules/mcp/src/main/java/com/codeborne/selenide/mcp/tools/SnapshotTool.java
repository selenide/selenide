package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class SnapshotTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{"mode":{"type":"string","enum":["action","assert","full"],"description":"Snapshot mode: action=interactive elements only, assert=interactive+text, full=all elements"},"selector":{"type":"string","description":"CSS selector to use as root (optional; defaults to document.body)"},"visibleOnly":{"type":"boolean","description":"Include only visible elements (default true)"},"maxDepth":{"type":"integer","description":"Maximum DOM depth to traverse (optional)"}}}
      """;

  private final BrowserSession session;
  private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  SnapshotTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_snapshot")
      .description("Get a structured snapshot of the page DOM for inspection and automation")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String mode = (String) request.arguments().getOrDefault("mode", "assert");
        String selector = (String) request.arguments().get("selector");
        Object visibleOnlyRaw = request.arguments().get("visibleOnly");
        boolean visibleOnly = visibleOnlyRaw == null || Boolean.TRUE.equals(visibleOnlyRaw);
        Integer maxDepth = (Integer) request.arguments().get("maxDepth");
        try {
          String result = snapshotBuilder.buildSnapshot(session.getDriver(), selector, mode, visibleOnly, maxDepth);
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result)))
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
