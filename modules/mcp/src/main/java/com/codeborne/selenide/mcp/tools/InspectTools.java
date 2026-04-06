package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class InspectTools {
  private InspectTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new SnapshotTool(session).spec(),
      new FindTool(session).spec(),
      new FindAllTool(session).spec(),
      new GetTextTool(session).spec(),
      new ConsoleLogsTool(session).spec()
    );
  }
}
