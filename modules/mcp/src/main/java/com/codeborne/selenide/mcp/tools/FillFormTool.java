package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

class FillFormTool extends McpTool {
  FillFormTool(BrowserSession session) {
    super(session, "browser_fill_form",
      "Fill multiple form fields in one call. Each field is {selector, value}.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "fields": {
            "type": "array",
            "description": "Ordered list of fields to fill",
            "items": {
              "type": "object",
              "properties": {
                "selector": {"type": "string", "description": "CSS selector, XPath, or text= selector"},
                "value":    {"type": "string", "description": "Value to set on the element"}
              },
              "required": ["selector", "value"]
            }
          }
        },
        "required": ["fields"]
      }
      """;
  }

  @Override
  @SuppressWarnings("unchecked")
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    List<Map<String, Object>> fields = (List<Map<String, Object>>) args.get("fields");
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("'fields' must be a non-empty array");
    }
    for (Map<String, Object> field : fields) {
      String selector = (String) field.get("selector");
      String value = (String) field.get("value");
      if (selector == null || value == null) {
        throw new IllegalArgumentException(
          "Each field requires both 'selector' and 'value'");
      }
      session.getDriver().$(resolve(selector)).setValue(value);
    }
    return success("Filled " + fields.size() + " fields");
  }
}
