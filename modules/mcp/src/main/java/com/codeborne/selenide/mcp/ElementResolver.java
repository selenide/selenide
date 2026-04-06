package com.codeborne.selenide.mcp;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;

/**
 * Parses selector strings into Selenium {@link By} locators.
 * <p>
 * Supported selector formats:
 * <ul>
 *   <li>CSS selectors: {@code #id}, {@code .class}, {@code tag.class}, {@code input[type=email]}</li>
 *   <li>XPath: strings starting with {@code //} or {@code ./}</li>
 *   <li>Text: {@code text=Sign In} (exact text match)</li>
 *   <li>Attribute shorthand: {@code data-testid=submit}, {@code name=email}</li>
 * </ul>
 * <p>
 * Limitation: attribute shorthand detection uses a heuristic — the part before {@code =}
 * must match {@code [a-zA-Z][a-zA-Z0-9-]*} and the selector must not contain {@code [}.
 * Complex CSS selectors with {@code =} (e.g. {@code div>input[type=text]}) are handled
 * correctly because they contain {@code [}, but unusual edge cases may be misidentified.
 */
public class ElementResolver {
  public By resolve(String selector) {
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
