package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class ExecuteJsTool extends McpTool {
  ExecuteJsTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_execute_js";
  }

  @Override
  String description() {
    return "Execute JavaScript code in the browser and return the result";
  }

  @Override
  String inputSchema() {
    return """
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
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String code = (String) args.get("code");
    Object result = session.getDriver().executeJavaScript(code);
    return success(result != null ? result.toString() : "null");
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return "(javascript)";
  }
}
