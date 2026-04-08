package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class HandleDialogTool extends McpTool {
  HandleDialogTool(BrowserSession session) {
    super(session, "browser_handle_dialog",
      "Accept or dismiss a browser dialog (alert, confirm, prompt)");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "accept": {
            "type": "boolean",
            "description": "True to accept, false to dismiss"
          },
          "promptText": {
            "type": "string",
            "description": "Text to enter into a prompt dialog before accepting"
          }
        },
        "required": ["accept"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    Boolean accept = (Boolean) args.get("accept");
    String promptText = (String) args.get("promptText");
    String dialogText;

    if (Boolean.TRUE.equals(accept)) {
      if (promptText != null) {
        dialogText = session.getDriver().modal().prompt(promptText);
      }
      else {
        dialogText = session.getDriver().modal().confirm();
      }
      return success("Dialog accepted. Text: " + dialogText);
    }
    else {
      dialogText = session.getDriver().modal().dismiss();
      return success("Dialog dismissed. Text: " + dialogText);
    }
  }

  @Override
  protected String errorContext(Map<String, Object> args) {
    return "(dialog)";
  }
}
