package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class TypeTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{"selector":{"type":"string","description":"CSS selector, XPath, or text= selector"},"text":{"type":"string","description":"Text to type into the element"},"submit":{"type":"boolean","description":"Press Enter after typing"}},"required":["selector","text"]}
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  TypeTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_type")
      .description("Type text into an element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        String text = (String) request.arguments().get("text");
        Boolean submit = (Boolean) request.arguments().get("submit");
        try {
          var by = resolver.resolve(selector);
          var element = session.getDriver().$(by).type(text);
          if (Boolean.TRUE.equals(submit)) {
            element.pressEnter();
          }
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Typed '" + text + "' into " + selector)))
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
