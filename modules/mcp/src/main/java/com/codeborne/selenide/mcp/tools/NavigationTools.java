package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class NavigationTools {
  private NavigationTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NavigateTool(session).spec(),
      new BackTool(session).spec(),
      new ForwardTool(session).spec(),
      new RefreshTool(session).spec(),
      new CloseTool(session).spec(),
      new GetUrlTool(session).spec()
    );
  }
}
