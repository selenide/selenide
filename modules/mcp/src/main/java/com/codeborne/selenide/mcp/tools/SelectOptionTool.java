package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class SelectOptionTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector for the select element"
          },
          "text": {
            "type": "string",
            "description": "Option text to select"
          },
          "value": {
            "type": "string",
            "description": "Option value to select"
          },
          "index": {
            "type": "integer",
            "description": "Option index to select"
          }
        },
        "required": ["selector"]
      }
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  SelectOptionTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_select_option")
      .description("Select an option in a <select> element by text, value, or index")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        String text = (String) request.arguments().get("text");
        String value = (String) request.arguments().get("value");
        Number index = (Number) request.arguments().get("index");
        try {
          var by = resolver.resolve(selector);
          var element = session.getDriver().$(by);
          if (text != null) {
            element.selectOption(text);
          }
          else if (value != null) {
            element.selectOptionByValue(value);
          }
          else if (index != null) {
            element.selectOption(index.intValue());
          }
          String selectedText = element.getSelectedOptionText();
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Selected '" + selectedText + "' in " + selector)))
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
