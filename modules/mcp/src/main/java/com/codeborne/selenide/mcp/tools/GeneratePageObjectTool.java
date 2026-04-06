package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class GeneratePageObjectTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{
      "className":{"type":"string","description":"Name of the page object class to generate"},
      "selector":{"type":"string","description":"CSS selector to scope the snapshot (optional)"}},
      "required":["className"]}
      """;

  private final BrowserSession session;
  private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  GeneratePageObjectTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_generate_page_object")
      .description("Generate a Selenide page object skeleton for the current page or a scoped element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String className = (String) request.arguments().get("className");
        String selector = (String) request.arguments().get("selector");
        try {
          String url = session.getDriver().getWebDriver().getCurrentUrl();
          String snapshot = snapshotBuilder.buildSnapshot(session.getDriver(), selector, "assert", true, null);
          String result = "PageObject: " + className + "\nURL: " + url + "\nElements:\n" + snapshot;
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, className))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
