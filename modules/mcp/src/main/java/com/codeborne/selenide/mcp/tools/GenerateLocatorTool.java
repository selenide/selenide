package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.LocatorGenerator;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GenerateLocatorTool {
  private static final String INPUT_SCHEMA = """
      {"type":"object","properties":{"selector":{"type":"string",
      "description":"CSS selector, XPath, or text= selector to identify the element"}},
      "required":["selector"]}
      """;

  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final LocatorGenerator locatorGenerator = new LocatorGenerator();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  GenerateLocatorTool(BrowserSession session) {
    this.session = session;
  }

  McpServerFeatures.SyncToolSpecification spec() {
    McpSchema.Tool tool = McpSchema.Tool.builder()
      .name("browser_generate_locator")
      .description("Generate ranked Selenide locators for a page element based on its attributes")
      .inputSchema(McpJsonDefaults.getMapper(), INPUT_SCHEMA)
      .build();

    return McpServerFeatures.SyncToolSpecification.builder()
      .tool(tool)
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          SelenideElement el = session.getDriver().$(resolver.resolve(selector));
          Map<String, Object> elementInfo = extractElementInfo(el);
          List<LocatorGenerator.RankedLocator> locators = locatorGenerator.rank(elementInfo);
          String result = formatLocators(locators, locatorGenerator.recommended(locators));
          return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result)))
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

  private Map<String, Object> extractElementInfo(SelenideElement el) {
    String classAttr = el.getAttribute("class");
    List<String> classes = (classAttr != null && !classAttr.isBlank())
      ? Arrays.asList(classAttr.trim().split("\\s+"))
      : List.of();

    Map<String, Object> info = new HashMap<>();
    info.put("tag", el.getTagName());
    putIfNotBlank(info, "id", el.getAttribute("id"));
    putIfNotBlank(info, "name", el.getAttribute("name"));
    putIfNotBlank(info, "testId", el.getAttribute("data-testid"));
    putIfNotBlank(info, "text", el.getText());
    if (!classes.isEmpty()) info.put("classes", classes);
    return info;
  }

  private void putIfNotBlank(Map<String, Object> map, String key, String value) {
    if (value != null && !value.isBlank()) {
      map.put(key, value);
    }
  }

  private String formatLocators(List<LocatorGenerator.RankedLocator> locators, String recommended) {
    StringBuilder sb = new StringBuilder();
    sb.append("Recommended: ").append(recommended).append("\n\n");
    sb.append("Ranked locators:\n");
    for (int i = 0; i < locators.size(); i++) {
      LocatorGenerator.RankedLocator l = locators.get(i);
      sb.append(i + 1).append(". [").append(l.confidence()).append("] ")
        .append(l.code()).append(" (strategy: ").append(l.strategy()).append(")\n");
    }
    return sb.toString();
  }
}
