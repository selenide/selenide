package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class CodegenTools {
  private CodegenTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new GenerateLocatorTool(session).spec(),
      new GeneratePageObjectTool(session).spec(),
      new DocsTool(session).spec()
    );
  }
}
