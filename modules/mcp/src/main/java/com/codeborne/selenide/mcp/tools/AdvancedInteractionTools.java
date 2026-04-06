package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class AdvancedInteractionTools {
  private AdvancedInteractionTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new DragAndDropTool(session).spec(),
      new SelectOptionTool(session).spec(),
      new UploadFileTool(session).spec(),
      new PressKeyTool(session).spec(),
      new HandleDialogTool(session).spec(),
      new ScreenshotTool(session).spec(),
      new ExecuteJsTool(session).spec()
    );
  }
}
