# MCP Additional Tools Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add 9 new MCP tools (tab management, resize, fill_form, wait_for, network inspection) to `modules/mcp` to bring selenide-mcp closer to Playwright MCP feature parity.

**Architecture:** Each tool subclasses `McpTool` in package `com.codeborne.selenide.mcp.tools`, package-private, with `name` / `description` / `inputSchema` / `execute(args)`. New `NetworkTools` group registrar mirrors the existing `NavigationTools`/`ElementInteractionTools`/`InspectTools`/`AdvancedInteractionTools` pattern. `SelenideMcpServer` registers the new group.

**Tech Stack:** Java 17, Gradle, Selenide (`SelenideDriver`), Selenium WebDriver, MCP SDK 1.1.3, BrowserUp Proxy 3.3.0, JUnit Jupiter, AssertJ.

**Code style reminders (project conventions enforced by Checkstyle):**
- 2-space indent, no tabs, no star imports, max line length 136, LF endings.
- Tool files are package-private; group registrars (`*Tools.java`) are public.
- No `@since`/javadoc requirement for internal MCP tools; existing tools have none.

---

## Task 1: Tab management tools (browser_tab_list, browser_tab_select, browser_tab_new, browser_tab_close)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabListTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabSelectTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabNewTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabCloseTool.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java`

- [ ] **Step 1: Create TabListTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.Map;

class TabListTool extends McpTool {
  TabListTool(BrowserSession session) {
    super(session, "browser_tab_list",
      "List all open browser tabs with index, handle, title and url");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    WebDriver driver = session.getDriver().getWebDriver();
    String activeHandle = driver.getWindowHandle();
    StringBuilder out = new StringBuilder();
    int i = 0;
    for (String handle : driver.getWindowHandles()) {
      driver.switchTo().window(handle);
      out.append('[').append(i++).append("] handle=\"").append(handle).append('"')
        .append(" title=\"").append(driver.getTitle()).append('"')
        .append(" url=\"").append(driver.getCurrentUrl()).append('"');
      if (handle.equals(activeHandle)) {
        out.append(" (active)");
      }
      out.append('\n');
    }
    driver.switchTo().window(activeHandle);
    return success(out.toString().trim());
  }
}
```

- [ ] **Step 2: Create TabSelectTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

class TabSelectTool extends McpTool {
  TabSelectTool(BrowserSession session) {
    super(session, "browser_tab_select",
      "Switch to a tab by 0-based index or by window handle");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "index":  {"type": "integer", "description": "0-based tab index"},
          "handle": {"type": "string",  "description": "Window handle string"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    Number indexRaw = (Number) args.get("index");
    String handle = (String) args.get("handle");
    if (indexRaw == null && handle == null) {
      throw new IllegalArgumentException("Provide either 'index' or 'handle'");
    }
    if (indexRaw != null && handle != null) {
      throw new IllegalArgumentException("Provide only one of 'index' or 'handle'");
    }
    WebDriver driver = session.getDriver().getWebDriver();
    String target;
    if (indexRaw != null) {
      List<String> handles = List.copyOf(driver.getWindowHandles());
      int index = indexRaw.intValue();
      if (index < 0 || index >= handles.size()) {
        throw new IllegalArgumentException(
          "Tab index " + index + " out of range (0.." + (handles.size() - 1) + ")");
      }
      target = handles.get(index);
    } else {
      target = handle;
    }
    driver.switchTo().window(target);
    return success("Switched to tab: " + target);
  }
}
```

- [ ] **Step 3: Create TabNewTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Map;

class TabNewTool extends McpTool {
  TabNewTool(BrowserSession session) {
    super(session, "browser_tab_new",
      "Open a new browser tab; optionally navigate to a URL");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "url": {"type": "string", "description": "Optional URL to open in the new tab"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String url = (String) args.get("url");
    WebDriver driver = session.getDriver().getWebDriver();
    driver.switchTo().newWindow(WindowType.TAB);
    if (url != null && !url.isEmpty()) {
      session.getDriver().open(url);
    }
    return success("Opened new tab" + (url != null ? " at " + url : ""));
  }
}
```

- [ ] **Step 4: Create TabCloseTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.Set;

class TabCloseTool extends McpTool {
  TabCloseTool(BrowserSession session) {
    super(session, "browser_tab_close",
      "Close a browser tab. With no args, closes the current tab");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "index":  {"type": "integer", "description": "0-based tab index"},
          "handle": {"type": "string",  "description": "Window handle string"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    WebDriver driver = session.getDriver().getWebDriver();
    Number indexRaw = (Number) args.get("index");
    String handle = (String) args.get("handle");
    String target;
    if (indexRaw == null && handle == null) {
      target = driver.getWindowHandle();
    } else if (indexRaw != null && handle != null) {
      throw new IllegalArgumentException("Provide only one of 'index' or 'handle'");
    } else if (indexRaw != null) {
      List<String> handles = List.copyOf(driver.getWindowHandles());
      int index = indexRaw.intValue();
      if (index < 0 || index >= handles.size()) {
        throw new IllegalArgumentException(
          "Tab index " + index + " out of range (0.." + (handles.size() - 1) + ")");
      }
      target = handles.get(index);
    } else {
      target = handle;
    }
    String active = driver.getWindowHandle();
    driver.switchTo().window(target);
    driver.close();
    Set<String> remaining = driver.getWindowHandles();
    if (remaining.isEmpty()) {
      return success("Closed last tab; no remaining tabs");
    }
    String next = target.equals(active) ? remaining.iterator().next() : active;
    driver.switchTo().window(next);
    return success("Closed tab: " + target);
  }
}
```

- [ ] **Step 5: Update NavigationTools.java to register the new tab tools**

Replace the full file contents:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class NavigationTools {
  private NavigationTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NavigateTool(session).spec(),
      new BackTool(session).spec(),
      new ForwardTool(session).spec(),
      new RefreshTool(session).spec(),
      new CloseTool(session).spec(),
      new GetUrlTool(session).spec(),
      new TabListTool(session).spec(),
      new TabSelectTool(session).spec(),
      new TabNewTool(session).spec(),
      new TabCloseTool(session).spec()
    );
  }
}
```

- [ ] **Step 6: Build and run MCP module check**

Run: `./gradlew :modules:mcp:check`
Expected: `BUILD SUCCESSFUL`, no Checkstyle or SpotBugs warnings.

- [ ] **Step 7: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabListTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabSelectTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabNewTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TabCloseTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java
git commit -m "mcp: add tab management tools (list/select/new/close)"
```

---

## Task 2: browser_resize tool

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ResizeTool.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java`

- [ ] **Step 1: Create ResizeTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.openqa.selenium.Dimension;

import java.util.Map;

class ResizeTool extends McpTool {
  ResizeTool(BrowserSession session) {
    super(session, "browser_resize",
      "Resize the browser viewport to the given width and height (pixels)");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "width":  {"type": "integer", "description": "Width in pixels"},
          "height": {"type": "integer", "description": "Height in pixels"}
        },
        "required": ["width", "height"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    int width = ((Number) args.get("width")).intValue();
    int height = ((Number) args.get("height")).intValue();
    session.getDriver().getWebDriver().manage().window()
      .setSize(new Dimension(width, height));
    return success("Resized to " + width + "x" + height);
  }
}
```

- [ ] **Step 2: Update NavigationTools.java to register ResizeTool**

Insert `new ResizeTool(session).spec()` at the end of the `List.of(...)`:

```java
  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NavigateTool(session).spec(),
      new BackTool(session).spec(),
      new ForwardTool(session).spec(),
      new RefreshTool(session).spec(),
      new CloseTool(session).spec(),
      new GetUrlTool(session).spec(),
      new TabListTool(session).spec(),
      new TabSelectTool(session).spec(),
      new TabNewTool(session).spec(),
      new TabCloseTool(session).spec(),
      new ResizeTool(session).spec()
    );
  }
```

- [ ] **Step 3: Build and run MCP module check**

Run: `./gradlew :modules:mcp:check`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ResizeTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java
git commit -m "mcp: add browser_resize tool"
```

---

## Task 3: browser_fill_form tool

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FillFormTool.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ElementInteractionTools.java`

- [ ] **Step 1: Create FillFormTool.java**

```java
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
```

- [ ] **Step 2: Update ElementInteractionTools.java to register FillFormTool**

Replace the full file contents:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class ElementInteractionTools {
  private ElementInteractionTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new ClickTool(session).spec(),
      new TypeTool(session).spec(),
      new SetValueTool(session).spec(),
      new ClearTool(session).spec(),
      new HoverTool(session).spec(),
      new FillFormTool(session).spec()
    );
  }
}
```

- [ ] **Step 3: Build and run MCP module check**

Run: `./gradlew :modules:mcp:check`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FillFormTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ElementInteractionTools.java
git commit -m "mcp: add browser_fill_form tool"
```

---

## Task 4: browser_wait_for tool (with unit test for argument validation)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/WaitForTool.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/WaitForToolTest.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/InspectTools.java`

- [ ] **Step 1: Write the failing test (WaitForToolTest.java)**

```java
package com.codeborne.selenide.mcp.tools;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitForToolTest {

  @Test
  void rejectsZeroConditions() {
    assertThatThrownBy(() -> WaitForTool.parseCondition(Map.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("exactly one of");
  }

  @Test
  void rejectsMultipleConditions() {
    Map<String, Object> args = new HashMap<>();
    args.put("text", "hello");
    args.put("time", 1);
    assertThatThrownBy(() -> WaitForTool.parseCondition(args))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("exactly one of");
  }

  @Test
  void parsesTextCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("text", "hello"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TEXT);
    assertThat(c.value()).isEqualTo("hello");
  }

  @Test
  void parsesTextGoneCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("textGone", "bye"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TEXT_GONE);
    assertThat(c.value()).isEqualTo("bye");
  }

  @Test
  void parsesSelectorConditionDefaultsToVisible() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("selector", "#foo"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.SELECTOR_VISIBLE);
    assertThat(c.value()).isEqualTo("#foo");
  }

  @Test
  void parsesSelectorConditionHidden() {
    WaitForTool.Condition c = WaitForTool.parseCondition(
      Map.of("selector", "#foo", "state", "hidden"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.SELECTOR_HIDDEN);
    assertThat(c.value()).isEqualTo("#foo");
  }

  @Test
  void parsesTimeCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("time", 2.5));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TIME);
    assertThat(c.numericValue()).isEqualTo(2.5);
  }

  @Test
  void rejectsUnknownSelectorState() {
    assertThatThrownBy(() -> WaitForTool.parseCondition(
      Map.of("selector", "#foo", "state", "weird")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("state");
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :modules:mcp:test --tests WaitForToolTest`
Expected: FAIL — class `WaitForTool` does not exist.

- [ ] **Step 3: Create WaitForTool.java**

```java
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
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :modules:mcp:test --tests WaitForToolTest`
Expected: PASS — 8 tests.

- [ ] **Step 5: Update InspectTools.java to register WaitForTool**

Replace the full file contents:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class InspectTools {
  private InspectTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new SnapshotTool(session).spec(),
      new FindTool(session).spec(),
      new FindAllTool(session).spec(),
      new GetTextTool(session).spec(),
      new ConsoleLogsTool(session).spec(),
      new WaitForTool(session).spec()
    );
  }
}
```

- [ ] **Step 6: Run full MCP module check**

Run: `./gradlew :modules:mcp:check`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/WaitForTool.java \
        modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/WaitForToolTest.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/InspectTools.java
git commit -m "mcp: add browser_wait_for tool"
```

---

## Task 5: Network inspection tools (browser_network_requests, browser_network_request) + NetworkTools group

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkRequestsTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkRequestTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkTools.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`

- [ ] **Step 1: Create NetworkRequestsTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.browserup.bup.BrowserUpProxy;
import com.codeborne.selenide.mcp.BrowserSession;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

class NetworkRequestsTool extends McpTool {
  private static final int MAX_ENTRIES = 100;

  NetworkRequestsTool(BrowserSession session) {
    super(session, "browser_network_requests",
      "List recently captured network requests. Requires --proxy-enabled at startup. "
        + "Optional 'urlPattern' filters by URL substring.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "urlPattern": {"type": "string", "description": "Substring to match against the URL"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    SelenideProxyServer proxy = session.getDriver().getProxy();
    if (proxy == null) {
      throw new IllegalStateException(
        "Network capture requires --proxy-enabled at server startup");
    }
    String pattern = (String) args.get("urlPattern");
    List<HarEntry> entries = harEntries(proxy.getProxy());
    StringBuilder out = new StringBuilder();
    int start = Math.max(0, entries.size() - MAX_ENTRIES);
    int shown = 0;
    for (int i = start; i < entries.size(); i++) {
      HarEntry e = entries.get(i);
      String url = e.getRequest().getUrl();
      if (pattern != null && !url.contains(pattern)) continue;
      out.append(e.getRequest().getMethod()).append(' ').append(url)
        .append(" -> ").append(e.getResponse().getStatus()).append(' ')
        .append(nullToDash(e.getResponse().getContent().getMimeType())).append(' ')
        .append((long) e.getTime()).append("ms\n");
      shown++;
    }
    if (shown == 0) {
      return success("No matching network requests");
    }
    return success(out.toString().trim());
  }

  static List<HarEntry> harEntries(BrowserUpProxy proxy) {
    Har har = proxy.getHar();
    if (har == null) {
      proxy.newHar("selenide-mcp");
      return List.of();
    }
    return har.getLog().getEntries();
  }

  private static String nullToDash(@Nullable String s) {
    return s == null || s.isEmpty() ? "-" : s;
  }
}
```

- [ ] **Step 2: Create NetworkRequestTool.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarHeader;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

class NetworkRequestTool extends McpTool {
  NetworkRequestTool(BrowserSession session) {
    super(session, "browser_network_request",
      "Get full detail (headers, status, duration) for the most recent network "
        + "request whose URL contains 'urlPattern'. Requires --proxy-enabled.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "urlPattern": {"type": "string", "description": "Substring to match against the URL"}
        },
        "required": ["urlPattern"]
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    SelenideProxyServer proxy = session.getDriver().getProxy();
    if (proxy == null) {
      throw new IllegalStateException(
        "Network capture requires --proxy-enabled at server startup");
    }
    String pattern = (String) args.get("urlPattern");
    if (pattern == null || pattern.isEmpty()) {
      throw new IllegalArgumentException("'urlPattern' is required");
    }
    List<HarEntry> entries = NetworkRequestsTool.harEntries(proxy.getProxy());
    HarEntry match = null;
    for (int i = entries.size() - 1; i >= 0; i--) {
      if (entries.get(i).getRequest().getUrl().contains(pattern)) {
        match = entries.get(i);
        break;
      }
    }
    if (match == null) {
      throw new IllegalArgumentException("No network request matching: " + pattern);
    }
    return success(format(match));
  }

  private static String format(HarEntry e) {
    StringBuilder out = new StringBuilder();
    out.append(e.getRequest().getMethod()).append(' ')
      .append(e.getRequest().getUrl()).append('\n');
    out.append("Status: ").append(e.getResponse().getStatus()).append(' ')
      .append(e.getResponse().getStatusText()).append('\n');
    out.append("Duration: ").append((long) e.getTime()).append("ms\n");
    out.append("Request headers:\n");
    appendHeaders(out, e.getRequest().getHeaders());
    out.append("Response headers:\n");
    appendHeaders(out, e.getResponse().getHeaders());
    return out.toString().trim();
  }

  private static void appendHeaders(StringBuilder out, List<HarHeader> headers) {
    for (HarHeader h : headers) {
      out.append("  ").append(h.getName()).append(": ").append(h.getValue()).append('\n');
    }
  }
}
```

- [ ] **Step 3: Create NetworkTools.java**

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class NetworkTools {
  private NetworkTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NetworkRequestsTool(session).spec(),
      new NetworkRequestTool(session).spec()
    );
  }
}
```

- [ ] **Step 4: Update SelenideMcpServer.java to register NetworkTools**

Add the import:

```java
import com.codeborne.selenide.mcp.tools.NetworkTools;
```

In `start(String[] args)`, extend the builder chain (replace the existing block):

```java
    var builder = McpServer.sync(transport)
      .serverInfo("selenide-mcp", getVersion())
      .tools(NavigationTools.specs(session))
      .tools(ElementInteractionTools.specs(session))
      .tools(AdvancedInteractionTools.specs(session))
      .tools(InspectTools.specs(session))
      .tools(NetworkTools.specs(session));
```

- [ ] **Step 5: Build and run full MCP module check**

Run: `./gradlew :modules:mcp:check`
Expected: `BUILD SUCCESSFUL`.

Verify SpotBugs/Checkstyle do not complain about the new code; if `@SuppressFBWarnings` is needed for the JSON suppression or the unchecked cast, address per the existing tools' patterns (search the module for prior examples).

- [ ] **Step 6: Run full root check to catch any cross-module regressions**

Run: `./gradlew check`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkRequestsTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkRequestTool.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NetworkTools.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java
git commit -m "mcp: add network inspection tools (browser_network_requests, browser_network_request)"
```

---

## Task 6: Final verification

- [ ] **Step 1: Confirm Javadoc-for-site still builds**

Run: `./gradlew javadocForSite`
Expected: `BUILD SUCCESSFUL`. (Per CLAUDE.md: this gate must hold before push.)

- [ ] **Step 2: Inspect the new tool list**

Run: `git diff main..HEAD --stat` and verify only files under `modules/mcp/` and `docs/superpowers/` are affected, no spurious changes.

- [ ] **Step 3: Report back to user**

Summarize: branch `worktree-mcp-additional-tools` contains 9 new tools across 6 commits, all checks green, ready to review/merge.

---

## Notes for the implementer

- The `BrowserUp` HAR API: `BrowserUpProxy.getHar()` returns a `de.sstoehr.harreader.model.Har` (from the `de.sstoehr:har-reader` dependency). It is `null` until `newHar(...)` is called. The plan lazily starts HAR on first network-tool call (`NetworkRequestsTool.harEntries`). This means traffic before the first network-tool call is not captured — acceptable for the MCP use case.
- `WindowType.TAB` import is `org.openqa.selenium.WindowType`.
- `Condition` static imports already used elsewhere in the codebase: `appear`, `disappear`, `text`. `Selectors.byTagName` for `body`.
- `SelenideProxyServer.getProxy()` returns the underlying `BrowserUpProxy` (`com.browserup.bup.BrowserUpProxy`).
- The MCP module depends only on `modules:core` and the MCP SDK. BrowserUp/HarReader classes (`com.browserup.bup.*`, `com.browserup.harreader.model.*`) come transitively through `modules:core`. If a compile-time visibility issue arises (e.g., the dependency is `runtimeOnly` on the core side), add `implementation "com.browserup:browserup-proxy-core:..."` to `modules/mcp/build.gradle` matching the version from the root `build.gradle`. Investigate before adding — the implementer should prefer not to add unnecessary deps.
- All new files use 2-space indentation, no tabs, LF line endings, newline at EOF, max 136 chars/line.