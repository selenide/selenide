# Selenide MCP Server

An [MCP (Model Context Protocol)](https://modelcontextprotocol.io) server that lets AI assistants control a real browser via [Selenide](https://selenide.org).

## Requirements

- Java 17+
- A browser installed (Chrome, Firefox, Edge, or Safari)

## Connecting to Claude Code

### Using `claude mcp add`

```bash
claude mcp add selenide-mcp 
```

With options:

```bash
claude mcp add selenide-mcp  \
  --browser=chrome --base-url=https://app.example.com --remote=http://selenium-hub:4444/wd/hub
```


### Using JSON config

Add to `.claude/settings.json` (project) or `~/.claude/settings.json` (global):

```json
{
  "mcpServers": {
    "selenide-mcp": {
      "command": "npx",
      "args": ["selenide-mcp", "--browser=chrome", "--remote=http://selenium-hub:4444/wd/hub"]
    }
  }
}
```

## Configuration Parameters

### Browser

| Parameter | Description | Default |
|---|---|---|
| `--browser=<name>` | Browser to use: `chrome`, `firefox`, `edge`, `safari` | `chrome` |
| `--browser-version=<ver>` | Browser version to use | latest installed |
| `--browser-size=<WxH>` | Browser window size, e.g. `1920x1080` | `1366x768` |
| `--browser-binary=<path>` | Path to browser executable | from PATH |
| `--browser-position=<XxY>` | Browser window position, e.g. `0x0` | none |
| `--headless` | Run browser in headless mode | `false` |

### Timeouts

| Parameter | Description | Default |
|---|---|---|
| `--timeout=<ms>` | Default element wait timeout (ms) | `4000` |
| `--polling-interval=<ms>` | Polling interval for conditions (ms) | `200` |
| `--page-load-timeout=<ms>` | Page load timeout (ms) | `30000` |
| `--page-load-strategy=<s>` | Page load strategy: `normal`, `eager`, `none` | `normal` |

### Navigation

| Parameter | Description | Default |
|---|---|---|
| `--base-url=<url>` | Base URL prepended to relative paths | `http://localhost:8080` |

### Remote WebDriver

| Parameter | Description | Default |
|---|---|---|
| `--remote=<url>` | Remote WebDriver URL, e.g. `http://selenium-hub:4444/wd/hub` | none |
| `--remote-read-timeout=<ms>` | Read timeout for remote connections (ms) | `90000` |
| `--remote-connection-timeout=<ms>` | Connection timeout for remote connections (ms) | `10000` |

### Proxy

| Parameter | Description | Default |
|---|---|---|
| `--proxy-enabled` | Enable built-in proxy | `false` |
| `--proxy-host=<host>` | Proxy host | none |
| `--proxy-port=<port>` | Proxy port | `0` |

### File Handling

| Parameter | Description | Default |
|---|---|---|
| `--downloads-folder=<path>` | Folder for downloaded files | `build/downloads` |
| `--reports-folder=<path>` | Folder for screenshots and reports | `build/reports/tests` |
| `--file-download=<mode>` | Download mode: `HTTPGET`, `PROXY`, `FOLDER`, `CDP` | `HTTPGET` |
| `--no-screenshots` | Disable screenshot capture on failure | screenshots enabled |
| `--no-save-page-source` | Disable page source saving on failure | saving enabled |

### Behavior

| Parameter | Description | Default |
|---|---|---|
| `--fast-set-value` | Set input values directly via JS (faster) | `false` |
| `--click-via-js` | Perform clicks via JS | `false` |
| `--webdriver-logs` | Enable WebDriver logs collection | `false` |
| `--no-reopen-browser-on-fail` | Do not reopen browser on failure | reopens on fail |
| `--text-check=<mode>` | Text assertion mode: `PARTIAL_TEXT`, `FULL_TEXT` | `PARTIAL_TEXT` |
| `--selector-mode=<mode>` | Selector mode: `CSS`, `Sizzle` | `CSS` |
| `--assertion-mode=<mode>` | Assertion mode: `STRICT`, `SOFT` | `STRICT` |

### Capabilities

| Parameter | Description |
|---|---|
| `--caps=<list>` | Comma-separated list of capabilities to enable |

Available capabilities:

- `codegen` — enables test code generation tools

Example: `--caps=codegen`
