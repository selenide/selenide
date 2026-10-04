# MCP additional tools (parity with Playwright MCP)

## Goal

Extend `selenide-mcp` with the capabilities Playwright MCP exposes but Selenide MCP currently lacks: tab management, generic wait condition, network request inspection, viewport resize, bulk form fill.

## Tools added

| Tool                       | Group               | Purpose                                                   |
|----------------------------|---------------------|-----------------------------------------------------------|
| `browser_tab_list`         | NavigationTools     | List all open tabs                                        |
| `browser_tab_select`       | NavigationTools     | Switch to tab by index or handle                          |
| `browser_tab_new`          | NavigationTools     | Open a new tab (optional URL)                             |
| `browser_tab_close`        | NavigationTools     | Close current tab, or a specific one                      |
| `browser_resize`           | NavigationTools     | Resize viewport `{width, height}`                         |
| `browser_fill_form`        | ElementInteraction  | Bulk-fill `{fields: [{selector, value}]}`                 |
| `browser_wait_for`         | InspectTools        | Wait for text / textGone / selector / time                |
| `browser_network_requests` | NetworkTools (new)  | List captured network requests                            |
| `browser_network_request`  | NetworkTools (new)  | Get one request by URL pattern (with headers, status)     |

Total: 9 new tools, 1 new tool-group registration.

## Detailed behavior

### Tabs

Each tab tool returns concise text content (no JSON) like the existing tools.

- `browser_tab_list` — no args. Returns lines: `[i] handle="..." title="..." url="..."` plus `(active)` marker.
- `browser_tab_select` — `{index?: int, handle?: string}` (exactly one). Uses `driver.getWebDriver().switchTo().window(handle)`.
- `browser_tab_new` — `{url?: string}`. Uses `switchTo().newWindow(WindowType.TAB)`; if `url` given, then `open(url)`.
- `browser_tab_close` — `{index?: int, handle?: string}` (both optional → closes current). After closing, if any tabs remain, switches to the first remaining handle so subsequent calls don't hit a dead driver.

### Resize

`browser_resize` — `{width: int, height: int}`. Calls `driver.getWebDriver().manage().window().setSize(new Dimension(w, h))`. Returns confirmation string.

### Fill form

`browser_fill_form` — `{fields: [{selector: string, value: string}, ...]}`. Iterates in order, calls `$(resolve(selector)).setValue(value)` on each. Returns `"Filled N fields"`. If any field fails, the existing `McpTool.error` path surfaces it.

### Wait for

`browser_wait_for` — exactly one of:
- `text: string` — wait for `$("body").shouldHave(text(text))`
- `textGone: string` — wait for `$("body").shouldNotHave(text(textGone))`
- `selector: string` + optional `state: "visible" | "hidden"` (default `visible`) — `$(resolve(selector)).should(visible ? appear : disappear)`
- `time: number` — seconds; `Thread.sleep(time * 1000)`

Validation: exactly one of `{text, textGone, selector, time}` must be present. Tool errors with a clear message otherwise. Selenide's configured timeout applies to the should-based waits; `time` ignores it.

### Network

Both tools require the BrowserUp proxy. On invocation:
1. Check `session.getDriver().getProxy()`. If null, return error: `"Network capture requires --proxy-enabled at server startup"`.
2. Get the underlying `BrowserUpProxy` via `selenideProxy.getProxy()`.
3. Lazy-start HAR: if `proxy.getHar() == null`, call `proxy.newHar("selenide-mcp")`. Idempotent via the `getHar()` check.

- `browser_network_requests` — `{urlPattern?: string}`. Returns up to 100 most recent entries (newest last) as lines: `<method> <url> -> <status> <contentType> <durationMs>ms`. If `urlPattern` provided, filter by substring match against URL.
- `browser_network_request` — `{urlPattern: string}` (required). Returns the most recent matching entry with method, URL, status, all request headers, all response headers, and duration. Errors if no match.

Registration: a new `NetworkTools.specs(session)` is added unconditionally to the server builder. Tools self-report the proxy-not-enabled error at call time. (Hiding the tools entirely would be cleaner but introduces conditional registration logic; the error path keeps the design simple and the tool list stable.)

## File structure

```
modules/mcp/src/main/java/com/codeborne/selenide/mcp/tools/
  TabListTool.java               (new)
  TabSelectTool.java             (new)
  TabNewTool.java                (new)
  TabCloseTool.java              (new)
  ResizeTool.java                (new)
  FillFormTool.java              (new)
  WaitForTool.java               (new)
  NetworkRequestsTool.java       (new)
  NetworkRequestTool.java        (new)
  NetworkTools.java              (new — group registrar)
  NavigationTools.java           (extend: +5 tools)
  ElementInteractionTools.java   (extend: +1 tool)
  InspectTools.java              (extend: +1 tool)

modules/mcp/src/main/java/com/codeborne/selenide/mcp/
  SelenideMcpServer.java         (extend: register NetworkTools group)
```

Each tool class extends `McpTool`, lives in package `com.codeborne.selenide.mcp.tools`, is package-private. Pattern is identical to the existing tools (`NavigateTool`, `SetValueTool`, etc.).

## Testing

Mirror the existing MCP module test convention: unit tests for non-trivial logic only — the thin wrapper tools have no direct tests. Where added value exists:

- `WaitForToolTest` — verify that zero conditions and >1 conditions both produce a clear error; verify each condition path dispatches correctly (via a small input-validation pre-check that we can test without a browser). The actual wait/should calls require a real browser and are not unit-testable here.

That is the only new test class — other tools are thin enough that unit testing offers no real coverage.

## Out of scope

- README/docs updates for the new tools.
- Integration tests using a real browser (no precedent in `modules/mcp` today).
- New `--caps=...` flag for network tools (always registered; error at call time when proxy is off).
- Page-level `waitForLoadState`-style helpers — not in Playwright MCP either.

## Risks / open questions

- `BrowserUpProxy.getHar()` returns `null` until `newHar(...)` is called. We lazy-start at first network-tool call, which means requests made before the first call aren't captured. Acceptable: tests using these tools will call them before the meaningful traffic. (Mitigation considered: start HAR at session creation when proxy is enabled. Rejected as it couples `BrowserSession` to network internals; reconsider if usage patterns show it's needed.)
- After closing the active tab, WebDriver requires switching to a surviving handle before further calls. The design above handles this; need to also handle "closed the last tab" by surfacing a clean message.
- `setValue` semantics on non-input elements differ from Playwright's `fill`. Acceptable — this mirrors Selenide's own form-filling behavior.