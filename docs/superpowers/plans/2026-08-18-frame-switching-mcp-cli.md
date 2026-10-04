# Frame/iframe switching for Selenide MCP and CLI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let an AI agent driving the browser through `modules/mcp`, and a human/script driving it through `modules/cli`, switch the underlying `SelenideDriver`'s context into an `<iframe>` and back out, so `browser_find`/`browser_snapshot`/`click`/etc. can reach elements inside a frame.

**Architecture:** Both front-ends already wrap the same core Selenide capability (`SelenideDriver.switchTo().frame(WebElement)` / `.defaultContent()`, in `SelenideTargetLocator`). Add two thin wrappers on each side, following the exact patterns each module already uses for its "context switching" family (MCP's `Tab*Tool` classes; CLI's zero/one-selector-arg `CommandInterpreter` handlers), then add both new action names to `McpCliParityTest`'s shared catalog so the two front-ends can't drift apart on this capability.

**Tech Stack:** Java 17, JUnit 5 (Jupiter), Mockito, AssertJ, Gradle multi-module build (`:modules:mcp`, `:modules:cli`).

**Spec:** No separate spec document — this was brainstormed as a bounded task (existing extension points on both sides, no new subsystem). The design was agreed in chat with the user on 2026-08-18: see the "Design" summary below, which is authoritative for this plan.

## Design summary (from brainstorming)

- MCP tool names: `browser_frame_select` (selector-only input) and `browser_frame_reset` (no input) — chosen over the issue's verb-first `browser_switch_frame`/`browser_switch_to_default_content` wording to match the existing noun-first "context switching" family (`browser_tab_select`, `browser_tab_list`, ...).
- CLI command names: `frame <selector>` and `defaultcontent`.
- Frame lookup is selector-only (CSS/XPath/`text=`) — no index/name overloads, even though core supports them (`SelenideTargetLocator.frame(int)` / `frame(String)`). Out of scope for this change.
- No separate "parent frame" (step up one level) tool/command — only reset-to-top. Core's `switchTo().parentFrame()` is not exposed.
- Both new capabilities are unit-tested only (mocked driver), matching every existing MCP tool test and CLI command test — no live-browser integration test exists for either module today, so none is added here.
- Both capabilities are added to `McpCliParityTest.CATALOG` since they are being built on both sides in lockstep.

## Global Constraints

- 2-space indentation, no tabs, max line length 136 characters, LF line endings, newline at end of file (per project `CLAUDE.md`).
- No star imports, no unused imports (Checkstyle `maxWarnings = 0`).
- Prefer static imports for readability (e.g. `IMPORT_BACK`, not `JavaCode.IMPORT_BACK`).
- Follow existing file/class patterns exactly — do not introduce new abstractions (no shared base class beyond the existing `McpTool`, no new `CommandInterpreter` helper method families).
- Before finishing, confirm `./gradlew javadocForSite` is not broken (per project `CLAUDE.md`).

---

### Task 1: MCP `browser_frame_select` tool

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameSelectTool.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameSelectToolTest.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java`

**Interfaces:**
- Consumes: `McpTool` base class (`modules/mcp/.../tools/McpTool.java`) — its default `inputSchema()` already returns `SELECTOR_SCHEMA` (`{"selector": string, required}`), its `resolve(String)` static helper turns a selector string into a `By`, and its `success(String)` helper builds the `CallToolResult`. `BrowserSession.getDriver()` returns a `SelenideDriver`, whose `$(By)` returns a `SelenideElement` and whose `switchTo()` returns a `SelenideTargetLocator` with `frame(WebElement)`.
- Produces: `FrameSelectTool` class (package-private, same shape as `TabSelectTool`) and its addition to `NavigationTools.specs(session)`. Task 5 (parity) depends on the tool being named exactly `browser_frame_select`.

- [ ] **Step 1: Write the failing test**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameSelectToolTest.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideTargetLocator;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameSelectToolTest {
  private final SelenideDriver driver = mock();
  private final SelenideElement element = mock();
  private final SelenideTargetLocator targetLocator = mock();
  private final FrameSelectTool tool = new FrameSelectTool(session(driver));

  @Test
  void switchesIntoFrameBySelector() {
    when(driver.$(any(By.class))).thenReturn(element);
    when(driver.switchTo()).thenReturn(targetLocator);

    McpSchema.CallToolResult result = tool.execute(Map.of("selector", "#iframe"));

    verify(targetLocator).frame(element);
    assertThat(text(result)).isEqualTo("Switched into frame: #iframe");
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.tools.FrameSelectToolTest"`
Expected: FAIL — compilation error, `FrameSelectTool` does not exist yet.

- [ ] **Step 3: Write minimal implementation**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameSelectTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FrameSelectTool extends McpTool {
  FrameSelectTool(BrowserSession session) {
    super(session, "browser_frame_select", "Switch the browser context into an iframe located by selector");
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    String selector = (String) args.get("selector");
    var by = resolve(selector);
    session.getDriver().switchTo().frame(session.getDriver().$(by));
    return success("Switched into frame: " + selector);
  }
}
```

Modify `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java` — add `new FrameSelectTool(session).spec()` right after `new TabCloseTool(session).spec()`:

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
      new FrameSelectTool(session).spec(),
      new ResizeTool(session).spec()
    );
  }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.tools.FrameSelectToolTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameSelectTool.java \
        modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameSelectToolTest.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java
git commit -m "#3371 add browser_frame_select MCP tool"
```

---

### Task 2: MCP `browser_frame_reset` tool

**Files:**
- Create: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameResetTool.java`
- Create: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameResetToolTest.java`
- Modify: `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java`

**Interfaces:**
- Consumes: `McpTool.EMPTY_SCHEMA` constant (`{"type":"object","properties":{}}`), same as `RefreshTool`. `SelenideDriver.switchTo().defaultContent()`.
- Produces: `FrameResetTool` class named exactly `browser_frame_reset`, registered in `NavigationTools.specs(session)` right after `FrameSelectTool`. Task 5 (parity) depends on this exact name.

- [ ] **Step 1: Write the failing test**

Create `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameResetToolTest.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideTargetLocator;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameResetToolTest {
  private final SelenideDriver driver = mock();
  private final SelenideTargetLocator targetLocator = mock();
  private final FrameResetTool tool = new FrameResetTool(session(driver));

  @Test
  void switchesToDefaultContent() {
    when(driver.switchTo()).thenReturn(targetLocator);

    McpSchema.CallToolResult result = tool.execute(Map.of());

    verify(targetLocator).defaultContent();
    assertThat(text(result)).isEqualTo("Switched to default content");
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.tools.FrameResetToolTest"`
Expected: FAIL — compilation error, `FrameResetTool` does not exist yet.

- [ ] **Step 3: Write minimal implementation**

Create `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameResetTool.java`:

```java
package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class FrameResetTool extends McpTool {
  FrameResetTool(BrowserSession session) {
    super(session, "browser_frame_reset", "Return the browser context to the top-level document");
  }

  @Override
  String inputSchema() {
    return EMPTY_SCHEMA;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    session.getDriver().switchTo().defaultContent();
    return success("Switched to default content");
  }
}
```

Modify `modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java` — add `new FrameResetTool(session).spec()` right after `new FrameSelectTool(session).spec()`:

```java
      new FrameSelectTool(session).spec(),
      new FrameResetTool(session).spec(),
      new ResizeTool(session).spec()
    );
  }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.tools.FrameResetToolTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/FrameResetTool.java \
        modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/FrameResetToolTest.java \
        modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/NavigationTools.java
git commit -m "#3371 add browser_frame_reset MCP tool"
```

---

### Task 3: CLI `frame <selector>` command

**Files:**
- Modify: `modules/cli/src/main/java/com/codeborne/selenide/cli/JavaCode.java`
- Modify: `modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java`
- Modify: `modules/cli/src/test/java/com/codeborne/selenide/cli/CommandInterpreterTest.java`

**Interfaces:**
- Consumes: `Locator.parse(String)` (`modules/cli/.../Locator.java`) → `record Locator(By by, String code, Set<String> imports)`. `Args.selectorRest()` joins every token after the command name. `RecordedStatement` (`code`, `staticImports`) and `PendingCommand(RecordedStatement, Runnable)`.
- Produces: `JavaCode.IMPORT_SWITCHTO` constant (used again by Task 4). The `"frame"` entry in `CommandInterpreter.COMMAND_NAMES` and its handler — Task 5 (parity) depends on the command name being exactly `frame`.

- [ ] **Step 1: Write the failing test**

Add to `modules/cli/src/test/java/com/codeborne/selenide/cli/CommandInterpreterTest.java` (after the `navigation()` test method):

```java
  @Test
  void frameSwitching() {
    assertThat(code("frame #iframe")).isEqualTo("switchTo().frame($(\"#iframe\"));");
    assertThat(interpreter.interpret("frame #iframe").statement().staticImports())
      .containsExactlyInAnyOrder("com.codeborne.selenide.Selenide.$", "com.codeborne.selenide.Selenide.switchTo");
  }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :modules:cli:test --tests "com.codeborne.selenide.cli.CommandInterpreterTest"`
Expected: FAIL — `CommandException: Unknown command: 'frame'`.

- [ ] **Step 3: Write minimal implementation**

Modify `modules/cli/src/main/java/com/codeborne/selenide/cli/JavaCode.java` — add the constant next to the other `IMPORT_*` fields:

```java
  static final String IMPORT_REFRESH = SELENIDE + "refresh";
  static final String IMPORT_SCREENSHOT = SELENIDE + "screenshot";
  static final String IMPORT_SWITCHTO = SELENIDE + "switchTo";
```

Modify `modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java`:

Add the static import at the top, alongside the other `JavaCode` static imports:

```java
import static com.codeborne.selenide.cli.JavaCode.IMPORT_SWITCHTO;
```

Add `"frame"` to `COMMAND_NAMES`:

```java
  private static final Set<String> COMMAND_NAMES = Set.of(
    "click", "doubleclick", "contextclick", "hover", "clear", "scrollto",
    "pressenter", "presstab", "pressescape",
    "setvalue", "type", "append", "selectoption", "selectradio",
    "setselected", "check", "uncheck",
    "open", "back", "forward", "refresh", "should", "screenshot", "frame");
```

Register the handler in `registerNavigation()`:

```java
  private void registerNavigation() {
    handlers.put("open", this::open);
    handlers.put("back", a -> new PendingCommand(RecordedStatement.of("back();", IMPORT_BACK), driver::back));
    handlers.put("forward", a -> new PendingCommand(RecordedStatement.of("forward();", IMPORT_FORWARD), driver::forward));
    handlers.put("refresh", a -> new PendingCommand(RecordedStatement.of("refresh();", IMPORT_REFRESH), driver::refresh));
    handlers.put("frame", this::frame);
  }
```

Add the `frame` builder method next to `open(Args)`:

```java
  private PendingCommand frame(Args args) {
    Locator locator = Locator.parse(args.selectorRest());
    Set<String> imports = new LinkedHashSet<>(locator.imports());
    imports.add(IMPORT_SWITCHTO);
    RecordedStatement statement = new RecordedStatement("switchTo().frame(" + locator.code() + ");", imports);
    return new PendingCommand(statement, () -> driver.switchTo().frame(driver.$(locator.by())));
  }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :modules:cli:test --tests "com.codeborne.selenide.cli.CommandInterpreterTest"`
Expected: PASS — including the pre-existing `commandNamesMatchRegisteredHandlers` test, since `"frame"` was added to both `COMMAND_NAMES` and `handlers` in this task.

- [ ] **Step 5: Commit**

```bash
git add modules/cli/src/main/java/com/codeborne/selenide/cli/JavaCode.java \
        modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java \
        modules/cli/src/test/java/com/codeborne/selenide/cli/CommandInterpreterTest.java
git commit -m "#3371 add 'frame' CLI command"
```

---

### Task 4: CLI `defaultcontent` command

**Files:**
- Modify: `modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java`
- Modify: `modules/cli/src/test/java/com/codeborne/selenide/cli/CommandInterpreterTest.java`

**Interfaces:**
- Consumes: `IMPORT_SWITCHTO` (added in Task 3). `driver.switchTo().defaultContent()`.
- Produces: the `"defaultcontent"` entry in `COMMAND_NAMES` and its handler — Task 5 (parity) depends on this exact name.

- [ ] **Step 1: Write the failing test**

Extend the `frameSwitching()` test added in Task 3 (in `CommandInterpreterTest.java`) to also cover `defaultContent`:

```java
  @Test
  void frameSwitching() {
    assertThat(code("frame #iframe")).isEqualTo("switchTo().frame($(\"#iframe\"));");
    assertThat(interpreter.interpret("frame #iframe").statement().staticImports())
      .containsExactlyInAnyOrder("com.codeborne.selenide.Selenide.$", "com.codeborne.selenide.Selenide.switchTo");

    assertThat(code("defaultContent")).isEqualTo("switchTo().defaultContent();");
    assertThat(interpreter.interpret("defaultContent").statement().staticImports())
      .containsExactly("com.codeborne.selenide.Selenide.switchTo");
  }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :modules:cli:test --tests "com.codeborne.selenide.cli.CommandInterpreterTest"`
Expected: FAIL — `CommandException: Unknown command: 'defaultcontent'`.

- [ ] **Step 3: Write minimal implementation**

Modify `modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java`:

Add `"defaultcontent"` to `COMMAND_NAMES`:

```java
  private static final Set<String> COMMAND_NAMES = Set.of(
    "click", "doubleclick", "contextclick", "hover", "clear", "scrollto",
    "pressenter", "presstab", "pressescape",
    "setvalue", "type", "append", "selectoption", "selectradio",
    "setselected", "check", "uncheck",
    "open", "back", "forward", "refresh", "should", "screenshot",
    "frame", "defaultcontent");
```

Register the handler in `registerNavigation()`, right after `"frame"`:

```java
    handlers.put("frame", this::frame);
    handlers.put("defaultcontent", a -> new PendingCommand(
      RecordedStatement.of("switchTo().defaultContent();", IMPORT_SWITCHTO),
      () -> driver.switchTo().defaultContent()));
  }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :modules:cli:test --tests "com.codeborne.selenide.cli.CommandInterpreterTest"`
Expected: PASS — including `commandNamesMatchRegisteredHandlers`.

- [ ] **Step 5: Commit**

```bash
git add modules/cli/src/main/java/com/codeborne/selenide/cli/CommandInterpreter.java \
        modules/cli/src/test/java/com/codeborne/selenide/cli/CommandInterpreterTest.java
git commit -m "#3371 add 'defaultcontent' CLI command"
```

---

### Task 5: Wire both actions into the MCP/CLI parity contract

**Files:**
- Modify: `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/McpCliParityTest.java`

**Interfaces:**
- Consumes: `SelenideCli.commandNames()` (already includes `"frame"`/`"defaultcontent"` after Tasks 3-4), `NavigationTools.specs(session)` tool names (already include `browser_frame_select`/`browser_frame_reset` after Tasks 1-2).
- Produces: nothing consumed by later tasks — this is the closing forcing-function check.

- [ ] **Step 1: Write the failing test (extend the existing catalog)**

Modify `modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/McpCliParityTest.java` — add two rows to `CATALOG`:

```java
  private static final List<SharedAction> CATALOG = List.of(
    new SharedAction("open", "browser_navigate"),
    new SharedAction("click", "browser_click"),
    new SharedAction("setvalue", "browser_set_value"),
    new SharedAction("type", "browser_type"),
    new SharedAction("clear", "browser_clear"),
    new SharedAction("hover", "browser_hover"),
    new SharedAction("selectoption", "browser_select_option"),
    // CLI splits key presses into pressEnter/pressTab/pressEscape; MCP has a generic browser_press_key.
    new SharedAction("pressenter", "browser_press_key"),
    new SharedAction("back", "browser_back"),
    new SharedAction("forward", "browser_forward"),
    new SharedAction("refresh", "browser_refresh"),
    new SharedAction("screenshot", "browser_screenshot"),
    new SharedAction("frame", "browser_frame_select"),
    new SharedAction("defaultcontent", "browser_frame_reset"));
```

(This is technically already "passing" since both sides were built in Tasks 1-4, but it is the step that proves the two front-ends actually agree — treat it as the test for this task.)

- [ ] **Step 2: Run test to verify it passes**

Run: `./gradlew :modules:mcp:test --tests "com.codeborne.selenide.mcp.tools.McpCliParityTest"`
Expected: PASS. If it fails, it means a name in `CATALOG` doesn't exactly match the tool/command name registered in Tasks 1-4 — go back and fix the mismatch (do not change the test to fit a wrong implementation name).

- [ ] **Step 3: Commit**

```bash
git add modules/mcp/src/test/java/com/codeborne/selenide/mcp/tools/McpCliParityTest.java
git commit -m "#3371 add frame switching to MCP/CLI parity catalog"
```

---

### Task 6: Documentation updates

**Files:**
- Modify: `modules/cli/README.md`
- Modify: `modules/cli/skills/selenide-cli/references/commands.md`
- Modify: `modules/mcp/src/main/resources/com/codeborne/selenide/mcp/docs/commands.md`

**Interfaces:**
- Consumes: nothing (docs only).
- Produces: nothing consumed by other tasks — this is documentation, not code.

- [ ] **Step 1: Update `modules/cli/README.md`**

In the "Recorded actions" fenced block (currently ending with `screenshot [name]`), add a line for the new commands:

```text
click <sel>                    setValue <sel> <text>   (alias: type)
append <sel> <text>            clear <sel>
selectOption <sel> <text>      selectRadio <sel> <value>
check <sel> | uncheck <sel>    setSelected <sel> <bool>
hover <sel>                    doubleClick <sel>        contextClick <sel>
scrollTo <sel>                pressEnter|pressTab|pressEscape <sel>
should <sel> <cond> [value]   back | forward | refresh
frame <sel>                   defaultContent
screenshot [name]
```

- [ ] **Step 2: Update `modules/cli/skills/selenide-cli/references/commands.md`**

In the "Recorded actions" table, add two rows right after the `screenshot [name]` row:

```markdown
| `screenshot [name]` | save PNG (to `reports-folder`) | `screenshot("<name>");` (default `screenshot`) |
| `frame <sel>` | `driver.switchTo().frame($(sel))` | `switchTo().frame($(...));` |
| `defaultContent` | `driver.switchTo().defaultContent()` | `switchTo().defaultContent();` |
```

- [ ] **Step 3: Update `modules/mcp/src/main/resources/com/codeborne/selenide/mcp/docs/commands.md`**

Add a new section after "## JavaScript" (the last section in the file):

````markdown

## Frames
```java
switchTo().frame($("iframe"))     // switch into an iframe
switchTo().defaultContent()       // return to the top-level document
```
````

- [ ] **Step 4: Commit**

```bash
git add modules/cli/README.md \
        modules/cli/skills/selenide-cli/references/commands.md \
        modules/mcp/src/main/resources/com/codeborne/selenide/mcp/docs/commands.md
git commit -m "#3371 document frame switching in CLI and MCP docs"
```

---

### Task 7: Full verification

**Files:** none (verification only).

**Interfaces:** none.

- [ ] **Step 1: Run the full unit test suite for both modules**

Run: `./gradlew :modules:mcp:check :modules:cli:check`
Expected: PASS — this also runs Checkstyle and SpotBugs (zero-warnings policy) over every file touched in Tasks 1-6.

- [ ] **Step 2: Run the whole-repo unit test suite**

Run: `./gradlew check`
Expected: PASS.

- [ ] **Step 3: Confirm the Javadoc site build is not broken**

Run: `./gradlew javadocForSite`
Expected: PASS (per project `CLAUDE.md`, this must not be broken before submitting a PR or pushing a branch).

- [ ] **Step 4: Final review pass**

Read through the diff (`git diff main...HEAD` or equivalent) once, end to end, confirming:
- `browser_frame_select` / `browser_frame_reset` and `frame` / `defaultcontent` are the only new public names introduced.
- No leftover TODOs, no changed behavior in unrelated tools/commands.
- All three doc files actually mention the new capability.

No commit for this task — it's a review checkpoint before considering the work done.
