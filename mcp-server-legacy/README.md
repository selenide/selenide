# selenide-mcp (deprecated)

This package has moved to [`@selenide/mcp`](https://www.npmjs.com/package/@selenide/mcp).

`selenide-mcp` still works — it's a thin shim that forwards to `@selenide/mcp` — but new
installs and configs should point at the scoped package name directly:

```bash
npx @selenide/mcp --browser=chrome --headless
```

```json
{
  "mcpServers": {
    "selenide-mcp": {
      "command": "npx",
      "args": ["@selenide/mcp", "--browser=chrome"]
    }
  }
}
```

See the [Selenide MCP docs](https://github.com/selenide/selenide/blob/main/modules/mcp/README.md)
for full configuration options.
