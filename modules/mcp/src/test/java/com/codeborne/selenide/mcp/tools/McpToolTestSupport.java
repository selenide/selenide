package com.codeborne.selenide.mcp.tools;

import io.modelcontextprotocol.spec.McpSchema;

final class McpToolTestSupport {
  private McpToolTestSupport() {
  }

  static String text(McpSchema.CallToolResult result) {
    return ((McpSchema.TextContent) result.content().get(0)).text();
  }
}
