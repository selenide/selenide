package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FindAllTool extends McpTool {
  FindAllTool(BrowserSession session) {
    super(session);
  }

  @Override
  String name() {
    return "browser_find_all";
  }

  @Override
  String description() {
    return "Find all elements matching a selector and return their properties";
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "CSS selector, XPath, or text= selector"
          }
        },
        "required": ["selector"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    ElementsCollection elements = session.getDriver().$$(resolve(selector));
    int size = elements.size();
    StringBuilder sb = new StringBuilder("Found " + size + " elements:\n");
    for (int i = 0; i < size; i++) {
      SelenideElement el = elements.get(i);
      sb.append("[").append(i).append("] ")
        .append(el.getTagName())
        .append(" | text='").append(el.getText()).append("'")
        .append(" | visible=").append(el.isDisplayed())
        .append("\n");
    }
    return success(sb.toString().stripTrailing());
  }
}
