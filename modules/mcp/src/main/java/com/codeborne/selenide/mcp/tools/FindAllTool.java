package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class FindAllTool {
  private static final String INPUT_SCHEMA = """
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

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  FindAllTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_find_all")
      .description("Find all elements matching a selector and return their properties")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          ElementsCollection elements = session.getDriver().$$(by);
          int size = elements.size();
          StringBuilder sb = new StringBuilder("Found " + size + " elements:\n");
          for (int i = 0; i < size; i++) {
            SelenideElement el = elements.get(i);
            String tag = el.getTagName();
            String text = el.getText();
            boolean visible = el.isDisplayed();
            sb.append("[").append(i).append("] ")
              .append(tag)
              .append(" | text='").append(text).append("'")
              .append(" | visible=").append(visible)
              .append("\n");
          }
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(sb.toString().stripTrailing())))
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
