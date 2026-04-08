package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class DragAndDropTool extends McpTool {
  DragAndDropTool(BrowserSession session) {
    super(session, "browser_drag_and_drop",
      "Drag an element and drop it onto another element");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "source": {
            "type": "string",
            "description": "Selector for the source element"
          },
          "target": {
            "type": "string",
            "description": "Selector for the target element"
          }
        },
        "required": ["source", "target"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String source = (String) args.get("source");
    String target = (String) args.get("target");
    var targetElement = session.getDriver().$(resolve(target));
    session.getDriver().$(resolve(source))
      .dragAndDrop(DragAndDropOptions.to(targetElement));
    return success("Dragged " + source + " to " + target);
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return (String) args.get("source");
  }
}
