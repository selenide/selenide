package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class SnapshotTool extends McpTool {
  private static final SnapshotBuilder SNAPSHOT_BUILDER = new SnapshotBuilder();

  SnapshotTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_snapshot";
  }

  @Override
  String description() {
    return "Get a structured snapshot of the page DOM for inspection and automation";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "mode": {
            "type": "string",
            "enum": ["action", "assert", "full"],
            "description": "action=interactive only, assert=interactive+text, full=all"
          },
          "selector": {
            "type": "string",
            "description": "CSS selector as root (optional; defaults to body)"
          },
          "visibleOnly": {
            "type": "boolean",
            "description": "Include only visible elements (default true)"
          },
          "maxDepth": {
            "type": "integer",
            "description": "Maximum DOM depth to traverse (optional)"
          }
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String mode = (String) args.getOrDefault("mode", "assert");
    String selector = (String) args.get("selector");
    Object visibleOnlyRaw = args.get("visibleOnly");
    boolean visibleOnly = visibleOnlyRaw == null
      || Boolean.TRUE.equals(visibleOnlyRaw);
    Number maxDepthRaw = (Number) args.get("maxDepth");
    Integer maxDepth = maxDepthRaw != null ? maxDepthRaw.intValue() : null;
    String result = SNAPSHOT_BUILDER.buildSnapshot(
      session.getDriver(), selector, mode, visibleOnly, maxDepth);
    return success(result);
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    return selector != null ? selector : "(page)";
  }
}
