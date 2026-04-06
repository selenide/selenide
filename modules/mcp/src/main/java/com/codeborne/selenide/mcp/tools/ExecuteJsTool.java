package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

class ExecuteJsTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "code": {
            "type": "string",
            "description": "JavaScript code to execute in the browser"
          }
        },
        "required": ["code"]
      }
      """;

  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  ExecuteJsTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_execute_js")
      .description("Execute JavaScript code in the browser and return the result")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String code = (String) request.arguments().get("code");
        try {
          Object result = session.getDriver().executeJavaScript(code);
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result != null ? result.toString() : "null")))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, "(javascript)"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
