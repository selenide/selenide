# Selectors & targeting

A selector is a single command-line argument. Quote any selector that contains spaces so the shell
passes it as one token. The CLI maps it to a Selenide locator for both the live action and the
generated code.

## Strategies

| Argument | Live locator | Generated code |
|---|---|---|
| `#id`, `.class`, `tag[attr=val]`, any CSS | `$(By.cssSelector(...))` | `$("...")` |
| `text=<text>` | `$(byText("<text>"))` | `$(byText("..."))` |
| `xpath=<expr>` | `$(byXpath("<expr>"))` | `$(byXpath("..."))` |
| `//...` (leading `//`) | `$(byXpath("//..."))` | `$(byXpath("//..."))` |

CSS is the default — anything that is not `text=`, `xpath=`, or a leading `//` is a CSS selector.

## Quoting (shell)

Because selectors/values are CLI arguments, use your shell's quoting for anything with spaces:

```bash
selenide click "input[name='first name']"     # CSS containing a space
selenide click "text=Sign in now"             # multi-word visible text
selenide setValue "#bio" "Hello there, world"  # multi-word value
selenide should "#title" text "Welcome, Jane"  # multi-word expected text
```

Single quotes inside a CSS/XPath selector are fine (wrap the whole arg in double quotes):

```bash
selenide click "//a[normalize-space()='Home']"
selenide click "li[data-id='42']"
```

## Choosing a strategy

- **`id` / `data-testid`** (via CSS `#id` or `[data-testid=...]`) — most stable; prefer for tests.
- **`text=` / `byText`** — great for buttons/links by visible label, but brittle under i18n.
- **`xpath=` / `//`** — for structural matches CSS can't express.

## Notes

- The token is passed to Selenide as-is; its usual CSS/XPath rules apply. `byText` matches the whole
  normalized text of an element.
- Escaping in generated code is automatic: `"`, `\`, and control characters in selector/value text
  are turned into valid Java string literals.
