package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.File;
import java.util.List;
import java.util.Map;

class UploadFileTool extends McpTool {
  UploadFileTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_upload_file";
  }

  @Override
  String description() {
    return "Upload one or more files using a file input element";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Selector for the file input element"
          },
          "paths": {
            "type": "array",
            "items": {"type": "string"},
            "description": "List of absolute file paths to upload"
          }
        },
        "required": ["selector", "paths"]
      }
      """;
  }

  @Override
  @SuppressWarnings("unchecked")
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    List<String> paths = (List<String>) args.get("paths");
    File[] files = paths.stream().map(File::new).toArray(File[]::new);
    session.getDriver().$(resolve(selector)).uploadFile(files);
    return success("Uploaded " + files.length + " file(s) to " + selector);
  }
}
