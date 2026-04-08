package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class GeneratePageObjectTool extends McpTool {
  private static final SnapshotBuilder SNAPSHOT_BUILDER = new SnapshotBuilder();

  GeneratePageObjectTool(BrowserSession session) {
    super(session, "browser_generate_page_object",
      "Generate a Selenide page object skeleton for the current page");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "className": {
            "type": "string",
            "description": "Name of the page object class to generate"
          },
          "selector": {
            "type": "string",
            "description": "CSS selector to scope the snapshot (optional)"
          }
        },
        "required": ["className"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String className = (String) args.get("className");
    String selector = (String) args.get("selector");
    String url = session.getDriver().url();
    String snapshot = SNAPSHOT_BUILDER.buildSnapshot(
      session.getDriver(), selector, "assert", true, null);
    return success(
      "PageObject: " + className + "\nURL: " + url + "\nElements:\n" + snapshot);
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return (String) args.get("className");
  }
}
