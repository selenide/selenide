# Selenide MCP Server Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an MCP server that lets AI agents automate browsers via Selenide, with smart waits, rich errors, and codegen support.

**Architecture:** Java MCP server in `modules/mcp/` using the official MCP SDK (`io.modelcontextprotocol.sdk:mcp:1.1.1`) with stdio transport. An npm wrapper in `mcp-server/` downloads and launches the fat JAR. Tools are organized in three groups: core (18), inspect (5), codegen (2).

**Tech Stack:** Java 17, Selenide (`:statics` + `:modules:core`), MCP SDK 1.1.1, Gradle shadowJar, Node.js (npm wrapper)

**Spec:** `docs/superpowers/specs/2026-04-06-selenide-mcp-server-design.md`

---

## File Structure

### Java MCP Server (`modules/mcp/`)

| File | Responsibility |
|------|---------------|
| `modules/mcp/build.gradle` | Gradle module config, shadowJar, dependencies |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java` | Entry point (`main`), MCP server init, tool registration |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/BrowserSession.java` | Lazy browser lifecycle (create/restart/close SelenideDriver) |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ElementResolver.java` | Parses selector strings into Selenium `By` objects |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ToolErrorHandler.java` | Enriches Selenide exceptions with context for LLM |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SnapshotBuilder.java` | Builds page snapshot (JS execution + result parsing) |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/LocatorGenerator.java` | Generates and ranks Selenide locators for elements |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigateTool.java` | `browser_navigate` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/BackTool.java` | `browser_back` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ForwardTool.java` | `browser_forward` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/RefreshTool.java` | `browser_refresh` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/CloseTool.java` | `browser_close` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClickTool.java` | `browser_click` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TypeTool.java` | `browser_type` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SetValueTool.java` | `browser_set_value` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClearTool.java` | `browser_clear` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HoverTool.java` | `browser_hover` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/DragAndDropTool.java` | `browser_drag_and_drop` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SelectOptionTool.java` | `browser_select_option` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/UploadFileTool.java` | `browser_upload_file` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/PressKeyTool.java` | `browser_press_key` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HandleDialogTool.java` | `browser_handle_dialog` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ScreenshotTool.java` | `browser_screenshot` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ExecuteJsTool.java` | `browser_execute_js` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetUrlTool.java` | `browser_get_url` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SnapshotTool.java` | `browser_snapshot` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindTool.java` | `browser_find` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindAllTool.java` | `browser_find_all` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetTextTool.java` | `browser_get_text` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ConsoleLogsTool.java` | `browser_console_logs` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GenerateLocatorTool.java` | `browser_generate_locator` tool |
| `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GeneratePageObjectTool.java` | `browser_generate_page_object` tool |
| `modules/mcp/src/main/resources/com/codeborne/selenide/mcp/snapshot.js` | JS script for building DOM snapshots in browser |

### Tests (`modules/mcp/src/test/java/`)

| File | What it tests |
|------|--------------|
| `com/codeborne/selenide/mcp/ElementResolverTest.java` | Selector parsing logic |
| `com/codeborne/selenide/mcp/ToolErrorHandlerTest.java` | Error enrichment logic |
| `com/codeborne/selenide/mcp/SnapshotBuilderTest.java` | Snapshot filtering logic |
| `com/codeborne/selenide/mcp/LocatorGeneratorTest.java` | Locator ranking logic |
| `com/codeborne/selenide/mcp/SelenideMcpServerTest.java` | Server startup and tool registration |

### npm Wrapper (`mcp-server/`)

| File | Responsibility |
|------|---------------|
| `mcp-server/package.json` | npm package metadata |
| `mcp-server/index.js` | JAR download, Java launch, stdio pipe |

### Config

| File | Change |
|------|--------|
| `settings.gradle` | Add `include ':modules:mcp'` |

---

## Task 1: Gradle Module Skeleton

Set up the `modules/mcp` Gradle module with dependencies and shadowJar.

**Files:**
- Create: `modules/mcp/build.gradle`
- Modify: `settings.gradle`

- [ ] **Step 1: Add module to settings.gradle**

Add the new module include to `settings.gradle`:

```gradle
include ':modules:mcp'
```

Add it after the existing module includes (e.g. after `include ':modules:video-recorder-testng'`).

- [ ] **Step 2: Create build.gradle for modules/mcp**

Create `modules/mcp/build.gradle`:

```gradle
plugins {
  id 'com.gradleup.shadow' version '9.0.0-beta12'
}

ext {
  artifactId = 'selenide-mcp'
  mcpSdkVersion = '1.1.1'
}

dependencies {
  implementation project(':statics')
  implementation "io.modelcontextprotocol.sdk:mcp:$mcpSdkVersion"

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.assertj:assertj-core:$assertjVersion") { transitive = false }
  testImplementation("org.mockito:mockito-core:$mockitoVersion")
}

shadowJar {
  archiveBaseName.set('selenide-mcp')
  archiveClassifier.set('')
  manifest {
    attributes 'Main-Class': 'com.codeborne.selenide.mcp.SelenideMcpServer'
  }
  mergeServiceFiles()
}

tasks.named('build') {
  dependsOn shadowJar
}
```

- [ ] **Step 3: Create source directories**

```bash
mkdir -p modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools
mkdir -p modules/mcp/src/main/resources/com/codeborne/selenide/mcp
mkdir -p modules/mcp/src/test/java/com/codeborne/selenide/mcp
```

- [ ] **Step 4: Create a minimal main class to verify compilation**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`:

```java
package com.codeborne.selenide.mcp;

public class SelenideMcpServer {
  public static void main(String[] args) {
    System.err.println("Selenide MCP Server starting...");
  }
}
```

- [ ] **Step 5: Verify the module compiles**

Run:
```bash
./gradlew :modules:mcp:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Verify shadowJar builds**

Run:
```bash
./gradlew :modules:mcp:shadowJar
```

Expected: BUILD SUCCESSFUL. File created at `modules/mcp/build/libs/selenide-mcp-<version>.jar`.

- [ ] **Step 7: Commit**

```bash
git add settings.gradle modules/mcp/
git commit -m "add modules/mcp Gradle skeleton with shadowJar"
```

---

## Task 2: BrowserSession — Lazy Browser Lifecycle

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/BrowserSession.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/BrowserSessionTest.java`

- [ ] **Step 1: Write the test**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/BrowserSessionTest.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrowserSessionTest {
  @Test
  void driverIsNotStartedUntilFirstAccess() {
    BrowserSession session = new BrowserSession(new SelenideConfig());
    assertThat(session.isStarted()).isFalse();
  }

  @Test
  void closingUnstartedSessionDoesNothing() {
    BrowserSession session = new BrowserSession(new SelenideConfig());
    session.close();
    assertThat(session.isStarted()).isFalse();
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.BrowserSessionTest"
```

Expected: FAIL — `BrowserSession` class not found.

- [ ] **Step 3: Implement BrowserSession**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/BrowserSession.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.jspecify.annotations.Nullable;

class BrowserSession {
  private final SelenideConfig config;
  @Nullable
  private SelenideDriver driver;

  BrowserSession(SelenideConfig config) {
    this.config = config;
  }

  SelenideDriver getDriver() {
    if (driver == null || !driver.hasWebDriverStarted()) {
      driver = new SelenideDriver(config);
    }
    return driver;
  }

  boolean isStarted() {
    return driver != null && driver.hasWebDriverStarted();
  }

  void close() {
    if (driver != null) {
      driver.close();
      driver = null;
    }
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.BrowserSessionTest"
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/
git commit -m "add BrowserSession with lazy browser lifecycle"
```

---

## Task 3: ElementResolver — Selector Parsing

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ElementResolver.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/ElementResolverTest.java`

- [ ] **Step 1: Write the test**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/ElementResolverTest.java`:

```java
package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class ElementResolverTest {
  private final ElementResolver resolver = new ElementResolver();

  @Test
  void cssSelectorById() {
    By by = resolver.resolve("#login-btn");
    assertThat(by).isEqualTo(By.cssSelector("#login-btn"));
  }

  @Test
  void cssSelectorByClass() {
    By by = resolver.resolve(".submit");
    assertThat(by).isEqualTo(By.cssSelector(".submit"));
  }

  @Test
  void cssSelectorByTagAndClass() {
    By by = resolver.resolve("button.primary");
    assertThat(by).isEqualTo(By.cssSelector("button.primary"));
  }

  @Test
  void cssSelectorWithAttribute() {
    By by = resolver.resolve("input[type=email]");
    assertThat(by).isEqualTo(By.cssSelector("input[type=email]"));
  }

  @Test
  void xpathAbsolute() {
    By by = resolver.resolve("//div[@id='content']");
    assertThat(by).isEqualTo(By.xpath("//div[@id='content']"));
  }

  @Test
  void xpathRelative() {
    By by = resolver.resolve(".//span");
    assertThat(by).isEqualTo(By.xpath(".//span"));
  }

  @Test
  void textSelector() {
    By by = resolver.resolve("text=Sign In");
    assertThat(by.toString()).contains("Sign In");
  }

  @Test
  void attributeSelector() {
    By by = resolver.resolve("data-testid=submit");
    assertThat(by.toString()).contains("data-testid");
    assertThat(by.toString()).contains("submit");
  }

  @Test
  void plainTagName() {
    By by = resolver.resolve("button");
    assertThat(by).isEqualTo(By.cssSelector("button"));
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.ElementResolverTest"
```

Expected: FAIL — `ElementResolver` class not found.

- [ ] **Step 3: Implement ElementResolver**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ElementResolver.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;

class ElementResolver {
  By resolve(String selector) {
    if (selector.startsWith("//") || selector.startsWith("./")) {
      return By.xpath(selector);
    }
    if (selector.startsWith("text=")) {
      return Selectors.byText(selector.substring("text=".length()));
    }
    if (isAttributeSelector(selector)) {
      int eqIndex = selector.indexOf('=');
      String attr = selector.substring(0, eqIndex);
      String value = selector.substring(eqIndex + 1);
      return Selectors.byAttribute(attr, value);
    }
    return By.cssSelector(selector);
  }

  private boolean isAttributeSelector(String selector) {
    if (!selector.contains("=")) return false;
    if (selector.contains("[")) return false;
    if (selector.startsWith("#") || selector.startsWith(".")) return false;
    String beforeEq = selector.substring(0, selector.indexOf('='));
    return beforeEq.matches("[a-zA-Z][a-zA-Z0-9-]*");
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.ElementResolverTest"
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/
git commit -m "add ElementResolver for unified selector parsing"
```

---

## Task 4: ToolErrorHandler — Enriched Error Responses

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ToolErrorHandler.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/ToolErrorHandlerTest.java`

- [ ] **Step 1: Write the test**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/ToolErrorHandlerTest.java`:

```java
package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToolErrorHandlerTest {
  private final ToolErrorHandler handler = new ToolErrorHandler();

  @Test
  void formatsGenericError() {
    String result = handler.formatError(
      new RuntimeException("Something went wrong"), "#btn"
    );
    assertThat(result).contains("Something went wrong");
    assertThat(result).contains("#btn");
  }

  @Test
  void includesSelectorInError() {
    String result = handler.formatError(
      new RuntimeException("timeout"), "text=Submit"
    );
    assertThat(result).contains("text=Submit");
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.ToolErrorHandlerTest"
```

Expected: FAIL — `ToolErrorHandler` class not found.

- [ ] **Step 3: Implement ToolErrorHandler**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/ToolErrorHandler.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.ex.ElementIsNotClickableError;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.UIAssertionError;

class ToolErrorHandler {
  String formatError(Throwable error, String selector) {
    StringBuilder sb = new StringBuilder();
    sb.append("Error: ").append(error.getMessage()).append("\n");
    sb.append("Selector: ").append(selector).append("\n");

    if (error instanceof ElementNotFound) {
      sb.append("Type: element_not_found\n");
      sb.append("Suggestion: Check that the selector is correct. ")
        .append("Use browser_snapshot to see available elements.\n");
    }
    else if (error instanceof ElementIsNotClickableError) {
      sb.append("Type: element_not_clickable\n");
      sb.append("Suggestion: Element may be overlapped by another element. ")
        .append("Try browser_execute_js to scroll or dismiss overlays.\n");
    }
    else if (error instanceof ElementShould) {
      sb.append("Type: condition_not_met\n");
      sb.append("Suggestion: Element was found but condition failed. ")
        .append("Use browser_find to inspect current element state.\n");
    }
    else if (error instanceof UIAssertionError) {
      sb.append("Type: assertion_error\n");
    }

    if (error.getCause() != null) {
      sb.append("Caused by: ").append(error.getCause().getMessage()).append("\n");
    }
    return sb.toString();
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.ToolErrorHandlerTest"
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/
git commit -m "add ToolErrorHandler for enriched error responses"
```

---

## Task 5: MCP Server Bootstrap with Navigation Tools

Wire up the MCP SDK, register the first batch of tools (navigate, back, forward, refresh, close, get_url), and make the server launchable.

**Files:**
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigateTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/BackTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ForwardTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/RefreshTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/CloseTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetUrlTool.java`

- [ ] **Step 1: Implement SelenideMcpServer with MCP SDK wiring**

Replace `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.sdk.McpServer;
import io.modelcontextprotocol.sdk.McpSyncServer;
import io.modelcontextprotocol.sdk.ServerCapabilities;
import io.modelcontextprotocol.sdk.SyncToolSpecification;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

import java.util.ArrayList;
import java.util.List;

public class SelenideMcpServer {
  private final BrowserSession session;
  private final List<SyncToolSpecification> tools = new ArrayList<>();

  public SelenideMcpServer(SelenideConfig config) {
    this.session = new BrowserSession(config);
  }

  void registerTool(SyncToolSpecification tool) {
    tools.add(tool);
  }

  public void start() {
    StdioServerTransportProvider transport =
      new StdioServerTransportProvider(new ObjectMapper());

    var builder = McpServer.sync(transport)
      .serverInfo("selenide-mcp", "1.0.0")
      .capabilities(ServerCapabilities.builder()
        .tools(true)
        .build());

    for (SyncToolSpecification tool : tools) {
      builder = builder.tool(tool);
    }

    McpSyncServer server = builder.build();
    System.err.println("Selenide MCP Server started with " + tools.size() + " tools");
  }

  public static void main(String[] args) {
    SelenideConfig config = parseConfig(args);
    SelenideMcpServer server = new SelenideMcpServer(config);

    // Register core navigation tools
    server.registerTool(new NavigateTool(server.session).spec());
    server.registerTool(new BackTool(server.session).spec());
    server.registerTool(new ForwardTool(server.session).spec());
    server.registerTool(new RefreshTool(server.session).spec());
    server.registerTool(new CloseTool(server.session).spec());
    server.registerTool(new GetUrlTool(server.session).spec());

    server.start();
  }

  static SelenideConfig parseConfig(String[] args) {
    SelenideConfig config = new SelenideConfig();
    for (String arg : args) {
      if (arg.startsWith("--browser=")) {
        config.browser(arg.substring("--browser=".length()));
      }
      else if (arg.equals("--headless")) {
        config.headless(true);
      }
      else if (arg.startsWith("--base-url=")) {
        config.baseUrl(arg.substring("--base-url=".length()));
      }
      else if (arg.startsWith("--timeout=")) {
        config.timeout(Long.parseLong(arg.substring("--timeout=".length())));
      }
    }
    return config;
  }
}
```

Note: The exact MCP SDK import paths may differ slightly. Check the actual SDK package structure after adding the dependency. The key classes are `McpServer`, `McpSyncServer`, `SyncToolSpecification`, `StdioServerTransportProvider`, `ServerCapabilities`, `Tool`, `CallToolResult`, `McpSchema.TextContent`. Adjust imports as needed based on the actual SDK.

- [ ] **Step 2: Implement NavigateTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigateTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;
import java.util.Map;

public class NavigateTool {
  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public NavigateTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "url": {
            "type": "string",
            "description": "URL to navigate to"
          }
        },
        "required": ["url"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_navigate")
        .description("Navigate to a URL. Smart waits ensure the page is loaded before returning.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String url = (String) request.arguments().get("url");
        try {
          session.getDriver().open(url);
          String currentUrl = session.getDriver()
            .getWebDriver().getCurrentUrl();
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Navigated to: " + currentUrl)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, url))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 3: Implement BackTool, ForwardTool, RefreshTool, CloseTool, GetUrlTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/BackTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class BackTool {
  private final BrowserSession session;

  public BackTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_back")
        .description("Navigate back to the previous page")
        .inputSchema(McpSchema.parseJsonSchema(
          "{\"type\":\"object\",\"properties\":{}}"))
        .build())
      .callHandler((exchange, request) -> {
        session.getDriver().back();
        String url = session.getDriver().getWebDriver().getCurrentUrl();
        return CallToolResult.builder()
          .content(List.of(new McpSchema.TextContent("Navigated back to: " + url)))
          .build();
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ForwardTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class ForwardTool {
  private final BrowserSession session;

  public ForwardTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_forward")
        .description("Navigate forward to the next page")
        .inputSchema(McpSchema.parseJsonSchema(
          "{\"type\":\"object\",\"properties\":{}}"))
        .build())
      .callHandler((exchange, request) -> {
        session.getDriver().forward();
        String url = session.getDriver().getWebDriver().getCurrentUrl();
        return CallToolResult.builder()
          .content(List.of(new McpSchema.TextContent("Navigated forward to: " + url)))
          .build();
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/RefreshTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class RefreshTool {
  private final BrowserSession session;

  public RefreshTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_refresh")
        .description("Reload the current page")
        .inputSchema(McpSchema.parseJsonSchema(
          "{\"type\":\"object\",\"properties\":{}}"))
        .build())
      .callHandler((exchange, request) -> {
        session.getDriver().refresh();
        return CallToolResult.builder()
          .content(List.of(new McpSchema.TextContent("Page refreshed")))
          .build();
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/CloseTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class CloseTool {
  private final BrowserSession session;

  public CloseTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_close")
        .description("Close the browser")
        .inputSchema(McpSchema.parseJsonSchema(
          "{\"type\":\"object\",\"properties\":{}}"))
        .build())
      .callHandler((exchange, request) -> {
        session.close();
        return CallToolResult.builder()
          .content(List.of(new McpSchema.TextContent("Browser closed")))
          .build();
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetUrlTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class GetUrlTool {
  private final BrowserSession session;

  public GetUrlTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_get_url")
        .description("Get the current page URL")
        .inputSchema(McpSchema.parseJsonSchema(
          "{\"type\":\"object\",\"properties\":{}}"))
        .build())
      .callHandler((exchange, request) -> {
        String url = session.getDriver().getWebDriver().getCurrentUrl();
        return CallToolResult.builder()
          .content(List.of(new McpSchema.TextContent(url)))
          .build();
      })
      .build();
  }
}
```

- [ ] **Step 4: Verify compilation**

Run:
```bash
./gradlew :modules:mcp:compileJava
```

Expected: BUILD SUCCESSFUL. If MCP SDK imports differ from what's shown above, adjust package paths based on compilation errors. The SDK javadoc at `javadoc.io` has the definitive API.

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/
git commit -m "add MCP server bootstrap with navigation tools"
```

---

## Task 6: Element Interaction Tools (Click, Type, SetValue, Clear, Hover)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClickTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TypeTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SetValueTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClearTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HoverTool.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`

- [ ] **Step 1: Implement ClickTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClickTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class ClickTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public ClickTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector (CSS, XPath, text=..., or data-testid=...)"
          },
          "doubleClick": {
            "type": "boolean",
            "description": "Double-click instead of single click",
            "default": false
          },
          "contextClick": {
            "type": "boolean",
            "description": "Right-click instead of single click",
            "default": false
          }
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_click")
        .description("Click an element. Selenide waits for element to be clickable.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          var element = session.getDriver().$(by);

          Boolean doubleClick = (Boolean) request.arguments().get("doubleClick");
          Boolean contextClick = (Boolean) request.arguments().get("contextClick");

          if (Boolean.TRUE.equals(doubleClick)) {
            element.doubleClick();
          } else if (Boolean.TRUE.equals(contextClick)) {
            element.contextClick();
          } else {
            element.click();
          }
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Clicked: " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 2: Implement TypeTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/TypeTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class TypeTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public TypeTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector"
          },
          "text": {
            "type": "string",
            "description": "Text to type character by character"
          },
          "submit": {
            "type": "boolean",
            "description": "Press Enter after typing",
            "default": false
          }
        },
        "required": ["selector", "text"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_type")
        .description("Type text into an element character by character. Useful for autosuggestion fields.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        String text = (String) request.arguments().get("text");
        try {
          var by = resolver.resolve(selector);
          var element = session.getDriver().$(by);
          element.type(text);
          if (Boolean.TRUE.equals(request.arguments().get("submit"))) {
            element.pressEnter();
          }
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Typed '" + text + "' into " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 3: Implement SetValueTool, ClearTool, HoverTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SetValueTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class SetValueTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public SetValueTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector"
          },
          "value": {
            "type": "string",
            "description": "Value to set"
          }
        },
        "required": ["selector", "value"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_set_value")
        .description("Set the value of an input field. Clears existing content first.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        String value = (String) request.arguments().get("value");
        try {
          var by = resolver.resolve(selector);
          session.getDriver().$(by).setValue(value);
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Set value '" + value + "' on " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ClearTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class ClearTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public ClearTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector"
          }
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_clear")
        .description("Clear the value of an input field")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          session.getDriver().$(by).clear();
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Cleared: " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HoverTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class HoverTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public HoverTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector"
          }
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_hover")
        .description("Hover over an element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          session.getDriver().$(by).hover();
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Hovered: " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 4: Register new tools in SelenideMcpServer.main()**

Add these lines to the `main()` method in `SelenideMcpServer.java`, after the existing navigation tool registrations:

```java
    server.registerTool(new ClickTool(server.session).spec());
    server.registerTool(new TypeTool(server.session).spec());
    server.registerTool(new SetValueTool(server.session).spec());
    server.registerTool(new ClearTool(server.session).spec());
    server.registerTool(new HoverTool(server.session).spec());
```

Add the corresponding imports at the top of `SelenideMcpServer.java`:

```java
import com.codeborne.selenide.mcp.tools.*;
```

Wait — no star imports in this project. Add individual imports:

```java
import com.codeborne.selenide.mcp.tools.ClickTool;
import com.codeborne.selenide.mcp.tools.TypeTool;
import com.codeborne.selenide.mcp.tools.SetValueTool;
import com.codeborne.selenide.mcp.tools.ClearTool;
import com.codeborne.selenide.mcp.tools.HoverTool;
```

- [ ] **Step 5: Verify compilation**

Run:
```bash
./gradlew :modules:mcp:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add modules/mcp/src/
git commit -m "add element interaction tools: click, type, setValue, clear, hover"
```

---

## Task 7: Remaining Core Tools (DragAndDrop, SelectOption, UploadFile, PressKey, HandleDialog, Screenshot, ExecuteJs)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/DragAndDropTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SelectOptionTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/UploadFileTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/PressKeyTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HandleDialogTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ScreenshotTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ExecuteJsTool.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`

- [ ] **Step 1: Implement DragAndDropTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/DragAndDropTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

import static com.codeborne.selenide.DragAndDropOptions.to;

public class DragAndDropTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public DragAndDropTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "source": {"type": "string", "description": "Source element selector"},
          "target": {"type": "string", "description": "Target element selector"}
        },
        "required": ["source", "target"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_drag_and_drop")
        .description("Drag an element and drop it onto another element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String source = (String) request.arguments().get("source");
        String target = (String) request.arguments().get("target");
        try {
          var sourceBy = resolver.resolve(source);
          var targetBy = resolver.resolve(target);
          session.getDriver().$(sourceBy)
            .dragAndDrop(to(session.getDriver().$(targetBy)));
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Dragged " + source + " to " + target)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, source))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 2: Implement SelectOptionTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SelectOptionTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class SelectOptionTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public SelectOptionTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Select element selector"},
          "text": {"type": "string", "description": "Option text to select"},
          "value": {"type": "string", "description": "Option value to select"},
          "index": {"type": "integer", "description": "Option index to select (0-based)"}
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_select_option")
        .description("Select an option in a <select> dropdown by text, value, or index")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          var element = session.getDriver().$(by);

          String text = (String) request.arguments().get("text");
          String value = (String) request.arguments().get("value");
          Integer index = (Integer) request.arguments().get("index");

          if (text != null) {
            element.selectOption(text);
          } else if (value != null) {
            element.selectOptionByValue(value);
          } else if (index != null) {
            element.selectOption(index);
          }

          String selected = element.getSelectedOptionText();
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Selected '" + selected + "' in " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 3: Implement UploadFileTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/UploadFileTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.io.File;
import java.util.List;

public class UploadFileTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public UploadFileTool(BrowserSession session) {
    this.session = session;
  }

  @SuppressWarnings("unchecked")
  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "File input element selector"},
          "paths": {
            "type": "array",
            "items": {"type": "string"},
            "description": "File paths to upload"
          }
        },
        "required": ["selector", "paths"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_upload_file")
        .description("Upload files to a file input element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        List<String> paths = (List<String>) request.arguments().get("paths");
        try {
          var by = resolver.resolve(selector);
          File[] files = paths.stream().map(File::new).toArray(File[]::new);
          session.getDriver().$(by).uploadFile(files);
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Uploaded " + paths.size() + " file(s) to " + selector)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 4: Implement PressKeyTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/PressKeyTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;
import org.openqa.selenium.Keys;

import java.util.List;

public class PressKeyTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public PressKeyTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Element selector (optional, defaults to active element)"},
          "key": {"type": "string", "description": "Key name (Enter, Escape, Tab, Backspace, etc.) or single character"}
        },
        "required": ["key"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_press_key")
        .description("Press a keyboard key on an element or the active element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String key = (String) request.arguments().get("key");
        String selector = (String) request.arguments().get("selector");
        try {
          var element = selector != null
            ? session.getDriver().$(resolver.resolve(selector))
            : session.getDriver().getFocusedElement();

          switch (key.toLowerCase()) {
            case "enter" -> element.pressEnter();
            case "escape" -> element.pressEscape();
            case "tab" -> element.pressTab();
            default -> {
              Keys seleniumKey = resolveKey(key);
              if (seleniumKey != null) {
                element.sendKeys(seleniumKey);
              } else {
                element.sendKeys(key);
              }
            }
          }
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Pressed key: " + key)))
            .build();
        }
        catch (Exception e) {
          String target = selector != null ? selector : "<focused element>";
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, target))))
            .isError(true)
            .build();
        }
      })
      .build();
  }

  private Keys resolveKey(String name) {
    try {
      return Keys.valueOf(name.toUpperCase());
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }
}
```

- [ ] **Step 5: Implement HandleDialogTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/HandleDialogTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

import static com.codeborne.selenide.ModalOptions.withExpectedText;

public class HandleDialogTool {
  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public HandleDialogTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "accept": {
            "type": "boolean",
            "description": "Accept (true) or dismiss (false) the dialog"
          },
          "text": {
            "type": "string",
            "description": "Text to enter in a prompt dialog"
          }
        },
        "required": ["accept"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_handle_dialog")
        .description("Handle a JavaScript alert, confirm, or prompt dialog")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        Boolean accept = (Boolean) request.arguments().get("accept");
        try {
          var modal = session.getDriver().modal();
          String action;
          if (Boolean.TRUE.equals(accept)) {
            modal.confirm();
            action = "accepted";
          } else {
            modal.dismiss();
            action = "dismissed";
          }
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              "Dialog " + action)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, "<dialog>"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 6: Implement ScreenshotTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ScreenshotTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;
import org.openqa.selenium.OutputType;

import java.util.Base64;
import java.util.List;

public class ScreenshotTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public ScreenshotTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {
            "type": "string",
            "description": "Element selector for element screenshot (omit for full page)"
          }
        }
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_screenshot")
        .description("Take a screenshot of the full page or a specific element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          byte[] screenshotBytes;
          if (selector != null) {
            var by = resolver.resolve(selector);
            screenshotBytes = session.getDriver().$(by)
              .getScreenshotAs(OutputType.BYTES);
          } else {
            screenshotBytes = session.getDriver()
              .getWebDriver().getScreenshotAs(OutputType.BYTES);
          }
          String base64 = Base64.getEncoder().encodeToString(screenshotBytes);
          return CallToolResult.builder()
            .content(List.of(new McpSchema.ImageContent(
              base64, "image/png")))
            .build();
        }
        catch (Exception e) {
          String target = selector != null ? selector : "<page>";
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, target))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

Note: `McpSchema.ImageContent` may have a different constructor signature. Check the SDK javadoc. If the SDK uses a different class for image content, use the appropriate one. Alternatively, return base64 as text content with a `data:image/png;base64,` prefix.

- [ ] **Step 7: Implement ExecuteJsTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ExecuteJsTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class ExecuteJsTool {
  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public ExecuteJsTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "code": {
            "type": "string",
            "description": "JavaScript code to execute in the browser"
          }
        },
        "required": ["code"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_execute_js")
        .description("Execute JavaScript code in the browser and return the result")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String code = (String) request.arguments().get("code");
        try {
          Object result = session.getDriver().executeJavaScript(code);
          String resultStr = result != null ? result.toString() : "null";
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(resultStr)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, "<javascript>"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 8: Register all new tools in SelenideMcpServer.main()**

Add to `main()`:

```java
    server.registerTool(new DragAndDropTool(server.session).spec());
    server.registerTool(new SelectOptionTool(server.session).spec());
    server.registerTool(new UploadFileTool(server.session).spec());
    server.registerTool(new PressKeyTool(server.session).spec());
    server.registerTool(new HandleDialogTool(server.session).spec());
    server.registerTool(new ScreenshotTool(server.session).spec());
    server.registerTool(new ExecuteJsTool(server.session).spec());
```

And add the corresponding imports.

- [ ] **Step 9: Verify compilation**

Run:
```bash
./gradlew :modules:mcp:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 10: Commit**

```bash
git add modules/mcp/src/
git commit -m "add remaining core tools: drag-drop, select, upload, key, dialog, screenshot, JS"
```

---

## Task 8: Inspect Tools (Snapshot, Find, FindAll, GetText, ConsoleLogs)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SnapshotBuilder.java`
- Create: `modules/mcp/src/main/resources/com/codeborne/selenide/mcp/snapshot.js`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SnapshotTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindAllTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetTextTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ConsoleLogsTool.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/SnapshotBuilderTest.java`

- [ ] **Step 1: Create the snapshot.js script**

Create `modules/mcp/src/main/resources/com/codeborne/selenide/mcp/snapshot.js`:

```javascript
(function(rootSelector, mode, visibleOnly, maxDepth) {
  var INTERACTIVE_TAGS = ['input','button','a','select','textarea','details','summary'];
  var TEXT_TAGS = ['h1','h2','h3','h4','h5','h6','p','span','label','td','th','li','dt','dd','caption','figcaption','legend'];
  var refCounter = 0;

  function isVisible(el) {
    if (!visibleOnly) return true;
    var rect = el.getBoundingClientRect();
    if (rect.width === 0 && rect.height === 0) return false;
    var style = window.getComputedStyle(el);
    return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0';
  }

  function shouldInclude(el, tag) {
    if (mode === 'full') return true;
    if (mode === 'action') return INTERACTIVE_TAGS.indexOf(tag) >= 0;
    // mode === 'assert' (default)
    if (INTERACTIVE_TAGS.indexOf(tag) >= 0) return true;
    if (TEXT_TAGS.indexOf(tag) >= 0 && el.textContent.trim().length > 0) return true;
    if (el.getAttribute('role')) return true;
    if (el.classList.contains('error') || el.classList.contains('alert') || el.classList.contains('warning')) return true;
    return false;
  }

  function bestSelectors(el) {
    var selectors = [];
    if (el.id) selectors.push('#' + el.id);
    var testId = el.getAttribute('data-testid') || el.getAttribute('data-test-id');
    if (testId) selectors.push('[data-testid="' + testId + '"]');
    if (el.name) selectors.push(el.tagName.toLowerCase() + '[name="' + el.name + '"]');
    var text = el.textContent.trim();
    if (text.length > 0 && text.length < 50) selectors.push('text=' + text);
    if (selectors.length === 0) {
      var tag = el.tagName.toLowerCase();
      var classes = Array.from(el.classList).slice(0, 2).join('.');
      selectors.push(classes ? tag + '.' + classes : tag);
    }
    return selectors;
  }

  function describe(el, depth) {
    if (maxDepth && depth > maxDepth) return null;
    var tag = el.tagName.toLowerCase();
    if (!isVisible(el)) return null;
    if (!shouldInclude(el, tag)) {
      // Even if this element isn't included, check children in full mode
      if (mode === 'full') return null;
      var childResults = [];
      for (var i = 0; i < el.children.length; i++) {
        var child = describe(el.children[i], depth + 1);
        if (child) childResults.push(child);
      }
      return childResults.length > 0 ? childResults : null;
    }

    refCounter++;
    var info = {
      ref: 'e' + refCounter,
      tag: tag,
      selectors: bestSelectors(el)
    };
    if (el.id) info.id = el.id;
    if (el.type) info.type = el.type;
    if (el.name) info.name = el.name;
    if (el.placeholder) info.placeholder = el.placeholder;
    var text = el.textContent.trim();
    if (text.length > 0 && text.length < 200) info.text = text;
    if (el.value !== undefined && el.value !== '') info.value = el.value;
    if (el.classList.length > 0) info.classes = Array.from(el.classList);
    info.visible = true;
    info.enabled = !el.disabled;
    if (el.getAttribute('href')) info.href = el.getAttribute('href');
    return info;
  }

  var root = rootSelector ? document.querySelector(rootSelector) : document.body;
  if (!root) return JSON.stringify({error: 'Root element not found: ' + rootSelector});

  var elements = [];
  var walker = document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT);
  var node = walker.nextNode();
  while (node) {
    var result = describe(node, 0);
    if (result) {
      if (Array.isArray(result)) {
        elements = elements.concat(result);
      } else {
        elements.push(result);
      }
    }
    node = walker.nextNode();
  }

  return JSON.stringify({
    url: window.location.href,
    title: document.title,
    viewport: {width: window.innerWidth, height: window.innerHeight},
    elementCount: elements.length,
    elements: elements
  });
})
```

- [ ] **Step 2: Implement SnapshotBuilder**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SnapshotBuilder.java`:

```java
package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

class SnapshotBuilder {
  private static final String SNAPSHOT_JS = loadSnapshotJs();

  String buildSnapshot(SelenideDriver driver, String selector,
                       String mode, boolean visibleOnly, Integer maxDepth) {
    String js = SNAPSHOT_JS + "(arguments[0], arguments[1], arguments[2], arguments[3])";
    return driver.executeJavaScript(js, selector, mode, visibleOnly, maxDepth);
  }

  private static String loadSnapshotJs() {
    try (var is = SnapshotBuilder.class.getResourceAsStream("snapshot.js")) {
      if (is == null) throw new IllegalStateException("snapshot.js not found in resources");
      return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
        .lines().collect(Collectors.joining("\n"));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to load snapshot.js", e);
    }
  }
}
```

- [ ] **Step 3: Implement SnapshotTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/SnapshotTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class SnapshotTool {
  private final BrowserSession session;
  private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public SnapshotTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "mode": {
            "type": "string",
            "enum": ["action", "assert", "full"],
            "default": "assert",
            "description": "action=interactive only, assert=interactive+text (default), full=entire DOM"
          },
          "selector": {
            "type": "string",
            "description": "CSS selector to limit snapshot scope"
          },
          "visibleOnly": {
            "type": "boolean",
            "default": true,
            "description": "Only include visible elements"
          },
          "maxDepth": {
            "type": "integer",
            "description": "Maximum DOM tree depth"
          }
        }
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_snapshot")
        .description("Capture page structure as an element tree. Returns elements with selectors and refs. " +
          "Better than screenshot for understanding page content.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        try {
          String mode = (String) request.arguments().getOrDefault("mode", "assert");
          String selector = (String) request.arguments().get("selector");
          Boolean visibleOnly = (Boolean) request.arguments().getOrDefault("visibleOnly", true);
          Integer maxDepth = (Integer) request.arguments().get("maxDepth");

          String json = snapshotBuilder.buildSnapshot(
            session.getDriver(), selector, mode, visibleOnly, maxDepth);
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(json)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, "<snapshot>"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 4: Implement FindTool, FindAllTool, GetTextTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class FindTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public FindTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Element selector"}
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_find")
        .description("Find an element and return its text, attributes, and visibility state")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          SelenideElement el = session.getDriver().$(by);
          String info = "Tag: " + el.getTagName() + "\n"
            + "Text: " + el.getText() + "\n"
            + "Visible: " + el.isDisplayed() + "\n"
            + "Enabled: " + el.isEnabled() + "\n"
            + "Value: " + el.getAttribute("value") + "\n"
            + "Classes: " + el.getAttribute("class");
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(info)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FindAllTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class FindAllTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public FindAllTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Element selector"}
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_find_all")
        .description("Find all matching elements and return a list with text and attributes")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          ElementsCollection elements = session.getDriver().$$(by);
          StringBuilder sb = new StringBuilder();
          sb.append("Found ").append(elements.size()).append(" elements:\n");
          int index = 0;
          for (SelenideElement el : elements) {
            sb.append("[").append(index++).append("] ")
              .append(el.getTagName())
              .append(" | text='").append(el.getText()).append("'")
              .append(" | visible=").append(el.isDisplayed())
              .append("\n");
          }
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(sb.toString())))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GetTextTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class GetTextTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public GetTextTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Element selector"}
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_get_text")
        .description("Get the text content of an element")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          String text = session.getDriver().$(by).getText();
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(text)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 5: Implement ConsoleLogsTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/ConsoleLogsTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.List;
import java.util.logging.Level;

public class ConsoleLogsTool {
  private final BrowserSession session;
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public ConsoleLogsTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "level": {
            "type": "string",
            "enum": ["ALL", "SEVERE", "WARNING", "INFO"],
            "default": "ALL",
            "description": "Minimum log level to include"
          }
        }
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_console_logs")
        .description("Get browser console log messages")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        try {
          String level = (String) request.arguments().getOrDefault("level", "ALL");
          Level minLevel = Level.parse(level);
          List<LogEntry> logs = session.getDriver()
            .getWebDriver().manage().logs().get(LogType.BROWSER).getAll();

          StringBuilder sb = new StringBuilder();
          for (LogEntry entry : logs) {
            if (entry.getLevel().intValue() >= minLevel.intValue()) {
              sb.append("[").append(entry.getLevel()).append("] ")
                .append(entry.getMessage()).append("\n");
            }
          }
          String result = sb.length() > 0 ? sb.toString() : "No console logs";
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, "<console>"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 6: Register inspect tools in SelenideMcpServer.main()**

Add to `main()`:

```java
    server.registerTool(new SnapshotTool(server.session).spec());
    server.registerTool(new FindTool(server.session).spec());
    server.registerTool(new FindAllTool(server.session).spec());
    server.registerTool(new GetTextTool(server.session).spec());
    server.registerTool(new ConsoleLogsTool(server.session).spec());
```

- [ ] **Step 7: Verify compilation**

Run:
```bash
./gradlew :modules:mcp:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add modules/mcp/src/
git commit -m "add inspect tools: snapshot, find, findAll, getText, consoleLogs"
```

---

## Task 9: Codegen Tools (GenerateLocator, GeneratePageObject)

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/LocatorGenerator.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GenerateLocatorTool.java`
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GeneratePageObjectTool.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/LocatorGeneratorTest.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/SelenideMcpServer.java`

- [ ] **Step 1: Write the test for LocatorGenerator**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/LocatorGeneratorTest.java`:

```java
package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LocatorGeneratorTest {
  private final LocatorGenerator generator = new LocatorGenerator();

  @Test
  void ranksIdHighest() {
    Map<String, Object> element = Map.of(
      "tag", "button",
      "id", "submit-btn",
      "text", "Submit",
      "classes", List.of("btn", "primary")
    );
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators.get(0).code()).contains("#submit-btn");
    assertThat(locators.get(0).confidence()).isEqualTo("stable");
  }

  @Test
  void ranksTestIdHighest() {
    Map<String, Object> element = Map.of(
      "tag", "button",
      "text", "Submit",
      "testId", "submit"
    );
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators.get(0).code()).contains("data-testid");
    assertThat(locators.get(0).confidence()).isEqualTo("stable");
  }

  @Test
  void includesTextLocator() {
    Map<String, Object> element = Map.of(
      "tag", "button",
      "text", "Submit"
    );
    List<LocatorGenerator.RankedLocator> locators = generator.rank(element);
    assertThat(locators).anyMatch(l -> l.code().contains("byText"));
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.LocatorGeneratorTest"
```

Expected: FAIL — `LocatorGenerator` class not found.

- [ ] **Step 3: Implement LocatorGenerator**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/LocatorGenerator.java`:

```java
package com.codeborne.selenide.mcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class LocatorGenerator {

  record RankedLocator(String code, String strategy, String confidence) {}

  List<RankedLocator> rank(Map<String, Object> element) {
    List<RankedLocator> locators = new ArrayList<>();
    String tag = (String) element.get("tag");

    String testId = (String) element.get("testId");
    if (testId != null) {
      locators.add(new RankedLocator(
        "$(byAttribute(\"data-testid\", \"" + testId + "\"))",
        "test-id", "stable"));
    }

    String id = (String) element.get("id");
    if (id != null) {
      locators.add(new RankedLocator(
        "$(\"#" + id + "\")", "id", "stable"));
    }

    String name = (String) element.get("name");
    if (name != null) {
      locators.add(new RankedLocator(
        "$(\"" + tag + "[name='" + name + "']\")",
        "name", "stable"));
    }

    @SuppressWarnings("unchecked")
    List<String> classes = (List<String>) element.get("classes");
    if (classes != null && !classes.isEmpty()) {
      String cssClasses = String.join(".", classes);
      locators.add(new RankedLocator(
        "$(\"" + tag + "." + cssClasses + "\")",
        "css", "medium"));
    }

    String text = (String) element.get("text");
    if (text != null && !text.isEmpty() && text.length() < 50) {
      locators.add(new RankedLocator(
        "$(byText(\"" + text + "\"))",
        "text", "fragile if i18n"));
    }

    if (locators.isEmpty()) {
      locators.add(new RankedLocator(
        "$(\"" + tag + "\")", "tag", "fragile"));
    }

    return locators;
  }

  String recommended(List<RankedLocator> locators) {
    return locators.isEmpty() ? null : locators.get(0).code();
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.LocatorGeneratorTest"
```

Expected: PASS

- [ ] **Step 5: Implement GenerateLocatorTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GenerateLocatorTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.ElementResolver;
import com.codeborne.selenide.mcp.LocatorGenerator;
import com.codeborne.selenide.mcp.LocatorGenerator.RankedLocator;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateLocatorTool {
  private final BrowserSession session;
  private final ElementResolver resolver = new ElementResolver();
  private final LocatorGenerator locatorGenerator = new LocatorGenerator();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public GenerateLocatorTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "selector": {"type": "string", "description": "Element selector or ref from snapshot"}
        },
        "required": ["selector"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_generate_locator")
        .description("Generate ranked Selenide locators for an element, ordered by stability")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String selector = (String) request.arguments().get("selector");
        try {
          var by = resolver.resolve(selector);
          var el = session.getDriver().$(by);

          Map<String, Object> elementInfo = new HashMap<>();
          elementInfo.put("tag", el.getTagName());
          String id = el.getAttribute("id");
          if (id != null && !id.isEmpty()) elementInfo.put("id", id);
          String name = el.getAttribute("name");
          if (name != null && !name.isEmpty()) elementInfo.put("name", name);
          String testId = el.getAttribute("data-testid");
          if (testId != null && !testId.isEmpty()) elementInfo.put("testId", testId);
          String text = el.getText();
          if (text != null && !text.isEmpty()) elementInfo.put("text", text);
          String cssClass = el.getAttribute("class");
          if (cssClass != null && !cssClass.isEmpty()) {
            elementInfo.put("classes", List.of(cssClass.split("\\s+")));
          }

          List<RankedLocator> locators = locatorGenerator.rank(elementInfo);
          StringBuilder sb = new StringBuilder();
          sb.append("Locators (best first):\n");
          for (RankedLocator loc : locators) {
            sb.append("  ").append(loc.code())
              .append(" [").append(loc.strategy())
              .append(", ").append(loc.confidence()).append("]\n");
          }
          sb.append("Recommended: ").append(locatorGenerator.recommended(locators));
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(sb.toString())))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, selector))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 6: Implement GeneratePageObjectTool**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/GeneratePageObjectTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.mcp.SnapshotBuilder;
import com.codeborne.selenide.mcp.ToolErrorHandler;
import io.modelcontextprotocol.sdk.McpSchema;
import io.modelcontextprotocol.sdk.McpSchema.Tool;
import io.modelcontextprotocol.sdk.McpSchema.CallToolResult;
import io.modelcontextprotocol.sdk.SyncToolSpecification;

import java.util.List;

public class GeneratePageObjectTool {
  private final BrowserSession session;
  private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
  private final ToolErrorHandler errorHandler = new ToolErrorHandler();

  public GeneratePageObjectTool(BrowserSession session) {
    this.session = session;
  }

  public SyncToolSpecification spec() {
    String schema = """
      {
        "type": "object",
        "properties": {
          "className": {"type": "string", "description": "Name for the PageObject class"},
          "selector": {"type": "string", "description": "CSS selector to limit scope (optional)"}
        },
        "required": ["className"]
      }
      """;

    return SyncToolSpecification.builder()
      .tool(Tool.builder()
        .name("browser_generate_page_object")
        .description("Analyze the page and return structured data for generating a Selenide PageObject. " +
          "Returns element names, types, roles, and best locators. " +
          "Use this data to write a Java PageObject class.")
        .inputSchema(McpSchema.parseJsonSchema(schema))
        .build())
      .callHandler((exchange, request) -> {
        String className = (String) request.arguments().get("className");
        String selector = (String) request.arguments().get("selector");
        try {
          String snapshot = snapshotBuilder.buildSnapshot(
            session.getDriver(), selector, "assert", true, null);
          String result = "PageObject: " + className + "\n"
            + "URL: " + session.getDriver().getWebDriver().getCurrentUrl() + "\n"
            + "Elements:\n" + snapshot;
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(result)))
            .build();
        }
        catch (Exception e) {
          return CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
              errorHandler.formatError(e, "<page>"))))
            .isError(true)
            .build();
        }
      })
      .build();
  }
}
```

- [ ] **Step 7: Register codegen tools conditionally in SelenideMcpServer**

Update `main()` in `SelenideMcpServer.java` to check for `--caps=codegen`:

```java
    boolean codegen = hasCapability(args, "codegen");
    if (codegen) {
      server.registerTool(new GenerateLocatorTool(server.session).spec());
      server.registerTool(new GeneratePageObjectTool(server.session).spec());
    }
```

Add helper method to `SelenideMcpServer`:

```java
  static boolean hasCapability(String[] args, String capability) {
    for (String arg : args) {
      if (arg.startsWith("--caps=") && arg.substring("--caps=".length()).contains(capability)) {
        return true;
      }
    }
    return false;
  }
```

- [ ] **Step 8: Verify all tests pass**

Run:
```bash
./gradlew :modules:mcp:test
```

Expected: All tests PASS

- [ ] **Step 9: Verify full compilation including shadowJar**

Run:
```bash
./gradlew :modules:mcp:shadowJar
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 10: Commit**

```bash
git add modules/mcp/src/
git commit -m "add codegen tools: generateLocator, generatePageObject"
```

---

## Task 10: npm Wrapper

**Files:**
- Create: `mcp-server/package.json`
- Create: `mcp-server/index.js`

- [ ] **Step 1: Create package.json**

Create `mcp-server/package.json`:

```json
{
  "name": "selenide-mcp",
  "version": "1.0.0",
  "description": "Selenide MCP server — browser automation with smart waits for AI agents",
  "bin": {
    "selenide-mcp": "index.js"
  },
  "keywords": ["mcp", "selenide", "browser", "testing", "automation", "selenium"],
  "license": "MIT",
  "repository": {
    "type": "git",
    "url": "https://github.com/selenide/selenide.git"
  },
  "engines": {
    "node": ">=16.0.0"
  }
}
```

- [ ] **Step 2: Create index.js**

Create `mcp-server/index.js`:

```javascript
#!/usr/bin/env node

const { execSync, spawn } = require('child_process');
const { existsSync, mkdirSync, createWriteStream } = require('fs');
const { join } = require('path');
const { get } = require('https');
const { homedir } = require('os');

const VERSION = require('./package.json').version;
const CACHE_DIR = join(homedir(), '.selenide-mcp');
const JAR_NAME = `selenide-mcp-${VERSION}.jar`;
const JAR_PATH = join(CACHE_DIR, JAR_NAME);
const MAVEN_BASE = 'https://repo1.maven.org/maven2/com/codeborne/selenide-mcp';
const JAR_URL = `${MAVEN_BASE}/${VERSION}/${JAR_NAME}`;

function checkJava() {
  try {
    const output = execSync('java -version 2>&1', { encoding: 'utf8' });
    const match = output.match(/version "(\d+)/);
    if (match && parseInt(match[1]) >= 17) return;
    process.stderr.write('Error: Java 17+ is required. Found: ' + output.split('\n')[0] + '\n');
    process.exit(1);
  } catch {
    process.stderr.write('Error: Java not found in PATH.\n');
    process.stderr.write('Install Java 17+: https://adoptium.net/\n');
    process.exit(1);
  }
}

function downloadJar() {
  return new Promise((resolve, reject) => {
    if (existsSync(JAR_PATH)) return resolve();
    mkdirSync(CACHE_DIR, { recursive: true });
    process.stderr.write(`Downloading selenide-mcp ${VERSION}...\n`);

    function download(url) {
      get(url, (res) => {
        if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
          return download(res.headers.location);
        }
        if (res.statusCode !== 200) {
          return reject(new Error(`Download failed: HTTP ${res.statusCode} from ${url}`));
        }
        const file = createWriteStream(JAR_PATH);
        res.pipe(file);
        file.on('finish', () => {
          file.close();
          process.stderr.write('Download complete.\n');
          resolve();
        });
      }).on('error', reject);
    }
    download(JAR_URL);
  });
}

async function main() {
  checkJava();
  await downloadJar();

  const args = process.argv.slice(2);
  const child = spawn('java', ['-jar', JAR_PATH, ...args], {
    stdio: ['pipe', 'pipe', 'inherit']
  });

  process.stdin.pipe(child.stdin);
  child.stdout.pipe(process.stdout);

  child.on('exit', (code) => process.exit(code || 0));
  process.on('SIGTERM', () => child.kill('SIGTERM'));
  process.on('SIGINT', () => child.kill('SIGINT'));
}

main().catch((err) => {
  process.stderr.write('Error: ' + err.message + '\n');
  process.exit(1);
});
```

- [ ] **Step 3: Verify the wrapper script is syntactically valid**

Run:
```bash
node -c mcp-server/index.js
```

Expected: No output (valid syntax)

- [ ] **Step 4: Commit**

```bash
git add mcp-server/
git commit -m "add npm wrapper for selenide-mcp"
```

---

## Task 11: End-to-End Smoke Test

Verify the full pipeline works: build the fat JAR, launch the server, send a tool list request via stdin.

**Files:** None (manual verification)

- [ ] **Step 1: Build the fat JAR**

Run:
```bash
./gradlew :modules:mcp:shadowJar
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Verify JAR is runnable**

Run:
```bash
java -jar modules/mcp/build/libs/selenide-mcp-*.jar --help 2>&1 || true
```

Expected: Server outputs "Selenide MCP Server starting..." to stderr (or the MCP SDK starts the server). The process may hang waiting for stdin — that's correct for stdio transport. Kill with Ctrl+C.

- [ ] **Step 3: Run all unit tests**

Run:
```bash
./gradlew :modules:mcp:test
```

Expected: All tests PASS

- [ ] **Step 4: Verify no checkstyle/spotbugs issues**

Run:
```bash
./gradlew :modules:mcp:check
```

Expected: BUILD SUCCESSFUL (zero warnings). If checkstyle or spotbugs fails, fix the issues:
- Line length > 136: break long lines
- Star imports: use specific imports
- Unused imports: remove them
- Tab characters: use 2-space indentation

- [ ] **Step 5: Final commit if any fixes were needed**

```bash
git add modules/mcp/src/
git commit -m "fix code style issues in modules/mcp"
```
