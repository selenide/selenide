package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byTagName;

class WaitForTool extends McpTool {
  enum Kind { TEXT, TEXT_GONE, SELECTOR_VISIBLE, SELECTOR_HIDDEN, TIME }

  record Condition(Kind kind, @Nullable String value, double numericValue) {
    static Condition of(Kind kind, String value) {
      return new Condition(kind, value, 0);
    }

    static Condition ofTime(double seconds) {
      return new Condition(Kind.TIME, null, seconds);
    }
  }

  WaitForTool(BrowserSession session) {
    super(session, "browser_wait_for",
      "Wait for a condition: text appearing/disappearing on page, "
        + "an element becoming visible/hidden, or a fixed time in seconds. "
        + "Exactly one of 'text', 'textGone', 'selector', 'time' must be provided.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "text":     {"type": "string",  "description": "Text to wait for on the page"},
          "textGone": {"type": "string",  "description": "Text to wait to disappear from the page"},
          "selector": {"type": "string",  "description": "Element selector to wait for"},
          "state":    {"type": "string",  "enum": ["visible", "hidden"], "description": "Selector state (default visible)"},
          "time":     {"type": "number",  "description": "Seconds to sleep"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    Condition c = parseCondition(args);
    switch (c.kind()) {
      case TEXT -> session.getDriver().$(byTagName("body")).shouldHave(text(c.value()));
      case TEXT_GONE -> session.getDriver().$(byTagName("body")).shouldNotHave(text(c.value()));
      case SELECTOR_VISIBLE -> session.getDriver().$(resolve(c.value())).should(appear);
      case SELECTOR_HIDDEN -> session.getDriver().$(resolve(c.value())).should(disappear);
      case TIME -> sleep((long) (c.numericValue() * 1000));
    }
    return success("Wait condition satisfied: " + c.kind());
  }

  static Condition parseCondition(Map<String, Object> args) {
    Map<String, Object> present = new LinkedHashMap<>();
    for (String key : new String[]{"text", "textGone", "selector", "time"}) {
      if (args.get(key) != null) {
        present.put(key, args.get(key));
      }
    }
    if (present.size() != 1) {
      throw new IllegalArgumentException(
        "browser_wait_for requires exactly one of: text, textGone, selector, time "
          + "(got " + present.size() + ")");
    }
    Map.Entry<String, Object> entry = present.entrySet().iterator().next();
    return switch (entry.getKey()) {
      case "text" -> Condition.of(Kind.TEXT, entry.getValue().toString());
      case "textGone" -> Condition.of(Kind.TEXT_GONE, entry.getValue().toString());
      case "time" -> Condition.ofTime(((Number) entry.getValue()).doubleValue());
      case "selector" -> {
        String state = args.get("state") == null ? "visible" : args.get("state").toString();
        yield switch (state) {
            case "visible" -> Condition.of(Kind.SELECTOR_VISIBLE, entry.getValue().toString());
            case "hidden" -> Condition.of(Kind.SELECTOR_HIDDEN, entry.getValue().toString());
            default -> throw new IllegalArgumentException(
              "Unknown state '" + state + "'; expected 'visible' or 'hidden'");
          };
      }
      default -> throw new IllegalStateException("unreachable");
    };
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Sleep interrupted", e);
    }
  }
}
