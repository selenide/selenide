package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.File;
import java.util.List;

class UploadFileTool {
  private static final String INPUT_SCHEMA = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector for the file input element"
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

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  UploadFileTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_upload_file")
      .description("Upload one or more files using a file input element")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        @SuppressWarnings("unchecked")
        List<String> paths = (List<String>) request.arguments().get("paths");
        try {
          var by = resolver.resolve(selector);
          File[] files = paths.stream().map(File::new).toArray(File[]::new);
          session.getDriver().$(by).uploadFile(files);
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent("Uploaded " + files.length + " file(s) to " + selector)))
            .isError(false)
            .build();
        }
        catch (Exception e) {
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
