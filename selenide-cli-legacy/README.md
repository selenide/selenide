# selenide-cli (deprecated)

This package has moved to [`@selenide/cli`](https://www.npmjs.com/package/@selenide/cli).

`selenide-cli` still works — it's a thin shim that forwards to `@selenide/cli` — but new installs
should point at the scoped package name directly:

```bash
npm install -g @selenide/cli
selenide open --headless https://selenide.org
```

See the [Selenide CLI docs](https://github.com/selenide/selenide/blob/main/modules/cli/README.md)
for full usage.
