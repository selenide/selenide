package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class NetworkTools {
  private NetworkTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NetworkRequestsTool(session).spec(),
      new NetworkRequestTool(session).spec()
    );
  }
}
