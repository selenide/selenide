package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FindTool extends McpTool {
  FindTool(BrowserSession session) {
    super(session, "browser_find",
      "Find a single element and return its properties");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    SelenideElement el = session.getDriver().$(resolve(selector));
    String result = "Tag: " + el.getTagName()
      + "\nText: " + el.getText()
      + "\nVisible: " + el.isDisplayed()
      + "\nEnabled: " + el.isEnabled()
      + "\nValue: " + el.getValue()
      + "\nClasses: " + el.getAttribute("class");
    return success(result);
  }
}
