package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;

abstract class McpTool {
  protected static final String EMPTY_SCHEMA =
    "{\"type\":\"object\",\"properties\":{}}";
  protected static final String SELECTOR_SCHEMA = """
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

  private static final ElementResolver RESOLVER = new ElementResolver();
  private static final ToolErrorHandler ERROR_HANDLER = new ToolErrorHandler();

  private final String name;
  private final String description;
  protected final BrowserSession session;

  McpTool(BrowserSession session, String name, String description) {
    this.session = session;
    this.name = name;
    this.description = description;
  }

  String name() {
    return name;
  }

  String description() {
    return description;
  }

  String inputSchema() {
    return SELECTOR_SCHEMA;
  }

  abstract McpSchema.CallToolResult execute(Map<String, Object> args);

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name(name())
      .description(description())
      .inputSchema(McpJsonDefaults.getMapper(), inputSchema())
      .build();
    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        try {
          return execute(request.arguments());
        }
        catch (Exception e) {
          return error(e, errorContext(request.arguments()));
        }
      })
      .build();
  }

  protected String errorContext(Map<String, Object> args) {
    Object selector = args.get("selector");
    return selector != null ? selector.toString() : name();
  }

  protected static By resolve(String selector) {
    return RESOLVER.resolve(selector);
  }

  protected McpSchema.CallToolResult success(String text) {
    return McpSchema.CallToolResult.builder()
      .content(List.of(new McpSchema.TextContent(text)))
      .isError(false)
      .build();
  }

  protected McpSchema.CallToolResult successImage(String base64Data, String mimeType) {
    return McpSchema.CallToolResult.builder()
      .content(List.of(new McpSchema.ImageContent(null, base64Data, mimeType)))
      .isError(false)
      .build();
  }

  protected McpSchema.CallToolResult error(Exception e, String context) {
    return McpSchema.CallToolResult.builder()
      .content(List.of(
        new McpSchema.TextContent(ERROR_HANDLER.formatError(e, context))))
      .isError(true)
      .build();
  }
}
