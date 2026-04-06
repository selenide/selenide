# Selenide MCP Server — Design Spec

## Overview

Selenide MCP Server is a Model Context Protocol server that enables AI agents (Claude, GPT, etc.) to automate browsers via Selenide and helps developers write idiomatic Selenide tests. It combines browser automation capabilities with Selenide's core strengths: smart waits and human-readable error messages.

### Goals

1. **Browser automation for AI agents** — navigate, click, type, inspect pages, verify state
2. **Help developers write Selenide tests** — generate locators, provide page structure for PageObject/test generation
3. **Competitive advantage over Playwright MCP** — smart waits, rich error context, ready-to-use selectors

### Non-goals (for MVP)

- Network mocking/interception (future: proxy integration)
- Cookie/storage management tools (future capability group)
- DevTools integration (tracing, video recording)
- Coordinate-based (vision) tools

## Architecture

### Module Structure

Two new components in the Selenide monorepo:

```
selenide/
├── modules/
│   └── mcp/                              # Java MCP server (Gradle module)
│       ├── build.gradle
│       └── src/main/java/
│           └── com/codeborne/selenide/mcp/
│               ├── SelenideMcpServer.java       # Entry point, tool registration
│               ├── BrowserSession.java          # Lazy browser lifecycle
│               ├── tools/
│               │   ├── core/                    # Navigate, Click, Type, ...
│               │   ├── inspect/                 # Snapshot, Find, ConsoleLogs
│               │   └── codegen/                 # GenerateLocator, GeneratePageObject
│               └── util/
│                   ├── ElementResolver.java     # Resolves selector string → SelenideElement
│                   └── ToolErrorHandler.java     # Enriched error responses
├── mcp-server/                           # npm wrapper
│   ├── package.json                      # "selenide-mcp" npm package
│   ├── index.js                          # Downloads JAR, launches java -jar
│   └── README.md
```

### Gradle Dependencies (`modules/mcp`)

```groovy
dependencies {
  implementation project(':modules:core')
  implementation project(':statics')
  implementation 'io.modelcontextprotocol:sdk:...'
}
```

Fat JAR built via `shadowJar` plugin — single file with all dependencies.

### Key Components

```
SelenideMcpServer (main)
    │
    ├── MCP SDK (stdio transport, JSON-RPC)
    │
    ├── BrowserSession
    │   ├── SelenideDriver (lazy init)
    │   ├── SelenideConfig (browser, timeout, etc.)
    │   └── auto-restart on crash
    │
    ├── ElementResolver
    │   ├── CSS selector → $(selector)
    │   ├── XPath → $x(xpath)
    │   ├── By text → $(byText("..."))
    │   └── By ref → reuse element from snapshot
    │
    └── ToolRegistry
        ├── CoreTools (18)
        ├── InspectTools (5)
        └── CodegenTools (2)  ← registered if --caps=codegen
```

## Browser Session — Lazy Lifecycle

In the spirit of Selenide, the browser is managed automatically:

- **Lazy start**: `SelenideDriver` is created on first tool call, not at server startup
- **Auto-restart**: If the browser crashes, it is recreated on the next tool call
- **Clean shutdown**: Browser closes when MCP session ends (SIGTERM/SIGINT)

```java
class BrowserSession {
  private SelenideDriver driver;  // null until first access
  private final SelenideConfig config;

  SelenideDriver getDriver() {
    if (driver == null || !driver.hasWebDriverStarted()) {
      driver = new SelenideDriver(config);
    }
    return driver;
  }

  void close() { /* shutdown driver if started */ }
}
```

## ElementResolver — Unified Selector Parsing

All tools accept `selector` as a string. `ElementResolver` determines the type automatically:

| Input | Strategy |
|-------|----------|
| `#id` or `.class` or `tag` or `tag.class` | CSS selector |
| Starts with `//` or `./` | XPath |
| `text=Sign In` | `Selectors.byText("Sign In")` |
| `data-testid=submit` | `Selectors.byAttribute("data-testid", "submit")` |
| `ref=e3` | Reuse element from last snapshot by ref ID |

This gives the LLM flexibility — no need to specify locator strategy explicitly.

## Tool Catalog

### Core (always available) — 18 tools

| Tool | Parameters | Description | Selenide mapping |
|------|-----------|-------------|-----------------|
| `browser_navigate` | `url` | Open URL | `selenideDriver.open(url)` |
| `browser_back` | — | Navigate back | `selenideDriver.back()` |
| `browser_forward` | — | Navigate forward | `selenideDriver.forward()` |
| `browser_refresh` | — | Reload page | `selenideDriver.refresh()` |
| `browser_close` | — | Close browser | `selenideDriver.close()` |
| `browser_click` | `selector`, `doubleClick?`, `contextClick?` | Click element | `$(selector).click()` / `.doubleClick()` / `.contextClick()` |
| `browser_type` | `selector`, `text`, `submit?` | Type text character by character | `$(selector).type(text)` |
| `browser_set_value` | `selector`, `value` | Set input value | `$(selector).setValue(value)` |
| `browser_clear` | `selector` | Clear input | `$(selector).clear()` |
| `browser_hover` | `selector` | Hover over element | `$(selector).hover()` |
| `browser_drag_and_drop` | `source`, `target` | Drag element to target | `$(source).dragAndDrop(to(target))` |
| `browser_select_option` | `selector`, `text?`, `value?`, `index?` | Select dropdown option | `$(selector).selectOption(...)` |
| `browser_upload_file` | `selector`, `paths[]` | Upload file(s) | `$(selector).uploadFile(...)` |
| `browser_press_key` | `key` | Press keyboard key | `$(selector).press(key)` |
| `browser_handle_dialog` | `accept`, `text?` | Handle alert/confirm/prompt | `selenideDriver.modal().confirm()` / `.dismiss()` |
| `browser_screenshot` | `selector?` | Take screenshot of page or element | `selenideDriver.screenshot()` / `$(sel).takeScreenshot()` |
| `browser_execute_js` | `code`, `args?` | Execute JavaScript | `selenideDriver.executeJavaScript(code)` |
| `browser_get_url` | — | Get current URL | `selenideDriver.getCurrentUrl()` |

### Inspect (always available) — 5 tools

| Tool | Parameters | Description |
|------|-----------|-------------|
| `browser_snapshot` | `mode?`, `selector?`, `visible_only?`, `max_depth?` | Page structure as element tree (see Snapshot section) |
| `browser_find` | `selector` | Find element, return text + attributes + visibility |
| `browser_find_all` | `selector` | Find all matching elements, return list with info |
| `browser_get_text` | `selector` | Get element text |
| `browser_console_logs` | `level?` | Browser console logs |

### Codegen (opt-in via `--caps=codegen`) — 2 tools

| Tool | Parameters | Description |
|------|-----------|-------------|
| `browser_generate_locator` | `selector` or `ref` | Ranked list of Selenide locators with stability confidence |
| `browser_generate_page_object` | `className`, `selector?` (scope) | Structured page description with best locators for each element |

**Total: 25 tools for MVP.**

## Snapshot — Page Inspection

The primary tool for LLM to "see" the page. Returns structured element tree, not screenshots.

### Modes

**`mode=action`** — Interactive elements only (button, input, a, select, textarea). For "fill the form", "click the button" scenarios.

**`mode=assert`** (default) — Interactive + text/semantic elements (h1-h6, p, span with text, label, td, li, validation errors, status messages). For verifying page state in tests.

**`mode=full`** — Complete DOM tree. For deep structural analysis or PageObject generation.

### Filter Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `mode` | `assert` | Element filtering mode |
| `selector` | — | Limit scope to subtree of specific element |
| `visible_only` | `true` | Only visible elements |
| `max_depth` | — | Limit DOM tree depth |

### Snapshot Response Format

```json
{
  "url": "https://example.com/login",
  "title": "Login — Example App",
  "viewport": {"width": 1280, "height": 720},
  "elements": [
    {
      "ref": "e1",
      "tag": "input",
      "type": "email",
      "id": "email",
      "placeholder": "Enter your email",
      "visible": true,
      "enabled": true,
      "value": "",
      "selectors": ["#email", "input[type=email]"]
    },
    {
      "ref": "e2",
      "tag": "button",
      "text": "Sign In",
      "visible": true,
      "enabled": false,
      "classes": ["btn", "btn-primary", "disabled"],
      "selectors": ["button.btn-primary", "text=Sign In"]
    }
  ]
}
```

### Key Differences from Playwright MCP

1. **`selectors` field** — each element comes with ready-to-use Selenide selectors. LLM picks one and passes it to the next tool call.
2. **`ref` field** — short session-scoped element ID. LLM can use `ref=e2` instead of a full selector. Token savings.
3. **`mode` parameter** — tailored filtering for different use cases (automation vs testing vs analysis).

## Error Handling — Competitive Advantage

When a tool call fails, the LLM receives a structured response with enough context to self-heal.

### Error Response Format

```json
{
  "success": false,
  "error": "Element not found",
  "selector": "#submit-btn",
  "timeout": "4s",
  "context": {
    "url": "https://example.com/login",
    "page_title": "Login — Example App",
    "nearby_elements": [
      {"tag": "button", "text": "Log In", "selector": "button.login-btn"},
      {"tag": "button", "text": "Cancel", "selector": "button.cancel-btn"}
    ]
  },
  "suggestion": "Did you mean 'button.login-btn' (text: 'Log In')?"
}
```

### Error Enrichment by Type

| Situation | What we add to response |
|-----------|------------------------|
| **Element not found** | Similar elements on page (fuzzy match by text, tag, attributes) |
| **Element not clickable** | What overlaps the element (overlay, modal), coordinates |
| **Element not visible** | Reason: `display:none`, outside viewport, inside closed `<details>`, in another iframe |
| **Stale element** | Automatic retry (Selenide already does this); if still fails — fresh snapshot |
| **Timeout** | Current element state at timeout (was it partially found, which conditions failed) |
| **Iframe/Shadow DOM** | Hint: "Element might be inside iframe 'X'" with list of iframes on page |

### Implementation

Selenide already produces good error messages. The MCP layer enriches them with context for LLM:

```java
class ToolErrorHandler {
  ToolResult handleError(Exception e, BrowserSession session, String selector) {
    var result = new ToolResult(false);
    result.setError(e.getMessage());  // Selenide message — already good

    if (e instanceof ElementNotFound) {
      result.setNearbyElements(findSimilarElements(session, selector));
      result.setSuggestion(suggestAlternative(session, selector));
    }
    if (e instanceof ElementShould) {
      result.setActualState(describeCurrentState(session, selector));
    }
    return result;
  }
}
```

`findSimilarElements` — finds elements with similar text, attributes, or location. This enables LLM self-healing on the next tool call without human intervention.

## Codegen Tools

### `browser_generate_locator`

Takes an element (by `ref` from snapshot or `selector`) and returns a **ranked list** of Selenide locators:

```json
{
  "locators": [
    {"code": "$(\"#submit-btn\")",         "strategy": "id",      "confidence": "stable"},
    {"code": "$(byText(\"Sign In\"))",     "strategy": "text",    "confidence": "fragile if i18n"},
    {"code": "$(\"button.btn-primary\")",   "strategy": "css",     "confidence": "medium"},
    {"code": "$(byAttribute(\"data-testid\", \"submit\"))", "strategy": "test-id", "confidence": "stable"}
  ],
  "recommended": "$(\"#submit-btn\")"
}
```

Ranking logic: `data-testid` / `id` > `name` > unique CSS > `byText` > XPath. Prefer stable selectors.

### `browser_generate_page_object`

Returns **structured data** about page elements with their best locators. The LLM writes the actual Java code, adapting it to the user's project style (naming, base class, package structure).

```json
{
  "pageName": "LoginPage",
  "url": "https://example.com/login",
  "elements": [
    {
      "name": "emailInput",
      "type": "input",
      "role": "text-field",
      "locator": "$(\"#email\")",
      "interactable": true
    },
    {
      "name": "signInButton",
      "type": "button",
      "role": "submit",
      "locator": "$(byText(\"Sign In\"))",
      "interactable": true
    },
    {
      "name": "errorMessage",
      "type": "text",
      "role": "error",
      "locator": "$(\"p.error\")",
      "interactable": false
    }
  ]
}
```

## npm Wrapper (`mcp-server/`)

### What `npx selenide-mcp` Does

```
npx selenide-mcp --browser=chrome --headless
        │
        ▼
   index.js
        │
        ├── 1. Check java >= 17 in PATH
        │      └── if missing → clear error with install instructions
        │
        ├── 2. Determine JAR version (from package.json)
        │
        ├── 3. Check cache ~/.selenide-mcp/selenide-mcp-1.0.0.jar
        │      └── if missing → download from Maven Central
        │
        ├── 4. Launch: java -jar ~/.selenide-mcp/selenide-mcp-1.0.0.jar [args]
        │
        └── 5. Pipe stdin/stdout between MCP client and Java process
               └── stderr forwarded to stderr (logs, not MCP traffic)
```

### Key Properties

- **Zero npm dependencies.** Only Node.js built-ins (`child_process`, `fs`, `https`, `path`). Instant `npx` startup.
- **JAR caching.** Downloaded once to `~/.selenide-mcp/`. Integrity verified by SHA checksum.
- **Clean stdio pipe.** `process.stdin.pipe(child.stdin)`, `child.stdout.pipe(process.stdout)`. No transformation — pure proxy.
- **Graceful shutdown.** `SIGTERM`/`SIGINT` → kills Java process.
- **Synchronized versioning.** npm package and JAR share the same version. `selenide-mcp@1.2.0` downloads `selenide-mcp-1.2.0.jar`.

## CLI Arguments

```
java -jar selenide-mcp.jar [options]

--browser=chrome|firefox|edge     (default: chrome)
--headless                        (default: true)
--base-url=http://...             (Selenide baseUrl)
--timeout=4000                    (default timeout in ms)
--caps=codegen                    (enable codegen capability group)
--proxy                           (enable Selenide proxy)
```

## Installation

### Claude Code

```json
{
  "mcpServers": {
    "selenide": {
      "command": "npx",
      "args": ["selenide-mcp", "--browser=chrome"]
    }
  }
}
```

### Other MCP clients

Same pattern — any MCP client that supports stdio transport.

## Future Capability Groups (Post-MVP)

| Group | Tools | Description |
|-------|-------|-------------|
| `network` | route, unroute, route_list, network_state | Network mocking via Selenide proxy |
| `storage` | cookie/localStorage/sessionStorage CRUD | Browser storage management |
| `devtools` | tracing, video recording | DevTools Protocol integration |
| `vision` | coordinate-based mouse operations | Pixel-level interaction |
