package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class ElementInteractionTools {
  private ElementInteractionTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new ClickTool(session).spec(),
      new TypeTool(session).spec(),
      new SetValueTool(session).spec(),
      new ClearTool(session).spec(),
      new HoverTool(session).spec()
    );
  }
}
