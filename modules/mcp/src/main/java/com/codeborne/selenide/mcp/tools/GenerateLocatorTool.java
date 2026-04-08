package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.LocatorGenerator;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GenerateLocatorTool extends McpTool {
  private static final LocatorGenerator LOCATOR_GENERATOR = new LocatorGenerator();

  GenerateLocatorTool(BrowserSession session) {
    super(session, "browser_generate_locator",
      "Generate ranked Selenide locators for an element");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Selector to identify the element"
          }
        },
        "required": ["selector"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    SelenideElement el = session.getDriver().$(resolve(selector));
    Map<String, Object> elementInfo = extractElementInfo(el);
    List<LocatorGenerator.RankedLocator> locators =
      LOCATOR_GENERATOR.rank(elementInfo);
    return success(
      formatLocators(locators, LOCATOR_GENERATOR.recommended(locators)));
  }

  private Map<String, Object> extractElementInfo(SelenideElement el) {
    String classAttr = el.getAttribute("class");
    List<String> classes = (classAttr != null && !classAttr.isBlank())
      ? Arrays.asList(classAttr.trim().split("\\s+"))
      : List.of();

    Map<String, Object> info = new HashMap<>();
    info.put("tag", el.getTagName());
    putIfNotBlank(info, "id", el.getAttribute("id"));
    putIfNotBlank(info, "name", el.getAttribute("name"));
    String testId = el.getAttribute("data-test-id");
    String testIdAttr = "data-test-id";
    if (testId == null || testId.isBlank()) {
      testId = el.getAttribute("data-testid");
      testIdAttr = "data-testid";
    }
    if (testId != null && !testId.isBlank()) {
      info.put("testId", testId);
      info.put("testIdAttr", testIdAttr);
    }
    putIfNotBlank(info, "text", el.getText());
    if (!classes.isEmpty()) info.put("classes", classes);
    return info;
  }

  private void putIfNotBlank(Map<String, Object> map,
                             String key, String value) {
    if (value != null && !value.isBlank()) {
      map.put(key, value);
    }
  }

  private String formatLocators(
    List<LocatorGenerator.RankedLocator> locators, String recommended) {
    StringBuilder sb = new StringBuilder();
    sb.append("Recommended: ").append(recommended).append("\n\n");
    sb.append("Ranked locators:\n");
    for (int i = 0; i < locators.size(); i++) {
      LocatorGenerator.RankedLocator l = locators.get(i);
      sb.append(i + 1).append(". [").append(l.confidence()).append("] ")
        .append(l.code())
        .append(" (strategy: ").append(l.strategy()).append(")\n");
    }
    return sb.toString();
  }
}
