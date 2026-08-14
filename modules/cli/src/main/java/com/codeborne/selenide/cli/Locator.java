package com.codeborne.selenide.cli;

import org.openqa.selenium.By;

import java.util.Set;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_BYTEXT;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_BYXPATH;
import static com.codeborne.selenide.cli.JavaCode.IMPORT_DOLLAR;
import static com.codeborne.selenide.cli.JavaCode.string;

/**
 * A selector token resolved into both a live Selenium {@link By} and the matching generated
 * {@code $(...)} code plus its required imports.
 *
 * <p>CSS by default; the prefixes {@code text=} and {@code xpath=} (or a leading {@code //}) select
 * {@code byText}/{@code byXpath} strategies.
 */
record Locator(By by, String code, Set<String> imports) {
  static Locator parse(String token) {
    if (token.startsWith("text=")) {
      String text = token.substring("text=".length());
      return new Locator(byText(text), "$(byText(" + string(text) + "))", Set.of(IMPORT_DOLLAR, IMPORT_BYTEXT));
    }
    if (token.startsWith("xpath=")) {
      return xpath(token.substring("xpath=".length()));
    }
    if (token.startsWith("//")) {
      return xpath(token);
    }
    return new Locator(By.cssSelector(token), "$(" + string(token) + ")", Set.of(IMPORT_DOLLAR));
  }

  private static Locator xpath(String expression) {
    return new Locator(byXpath(expression), "$(byXpath(" + string(expression) + "))", Set.of(IMPORT_DOLLAR, IMPORT_BYXPATH));
  }
}
