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
