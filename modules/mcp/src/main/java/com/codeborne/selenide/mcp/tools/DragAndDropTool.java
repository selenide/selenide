package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class DragAndDropTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "source": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector for the source element"
          },
          "target": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector for the target element"
          }
        },
        "required": ["source", "target"]
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  DragAndDropTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_drag_and_drop")
      .description("Drag an element and drop it onto another element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String source = (String) request.arguments().get("source");
        String target = (String) request.arguments().get("target");
        try {
          var sourceBy = resolver.resolve(source);
          var targetElement = session.getDriver().$(resolver.resolve(target));
          session.getDriver().$(sourceBy).dragAndDrop(DragAndDropOptions.to(targetElement));
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Dragged " + source + " to " + target)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, source))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
