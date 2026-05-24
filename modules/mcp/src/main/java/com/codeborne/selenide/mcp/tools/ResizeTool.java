package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.Dimension;

import java.util.Map;

class ResizeTool extends McpTool {
  ResizeTool(BrowserSession session) {
    super(session, "browser_resize",
      "Resize the browser viewport to the given width and height (pixels)");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "width":  {"type": "integer", "description": "Width in pixels"},
          "height": {"type": "integer", "description": "Height in pixels"}
        },
        "required": ["width", "height"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    int width = ((Number) args.get("width")).intValue();
    int height = ((Number) args.get("height")).intValue();
    session.getDriver().getWebDriver().manage().window()
      .setSize(new Dimension(width, height));
    return success("Resized to " + width + "x" + height);
  }
}
