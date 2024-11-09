package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class WebElementCommunicator implements ElementCommunicator {
  /**
   * Taken from
   * <a href="https://selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#getAttribute(java.lang.String)">spec</a>
   */
  @SuppressWarnings("SpellCheckingInspection")
  private static final Set<String> BOOLEAN_ATTRIBUTES = Set.of("async", "autofocus", "autoplay", "checked", "compact", "complete",
    "controls", "declare", "defaultchecked", "defaultselected", "defer", "disabled", "draggable", "ended", "formnovalidate",
    "hidden", "indeterminate", "iscontenteditable", "ismap", "itemscope", "loop", "multiple", "muted", "nohref", "noresize",
    "noshade", "novalidate", "nowrap", "open", "paused", "pubdate", "readonly", "readOnly", "required", "reversed", "scoped",
    "seamless", "seeking", "selected", "truespeed", "willvalidate");

  private static final String JS_GET_ATTRIBUTES = "return Array.from(arguments[0])" +
                                                  ".map(el => el[arguments[1]] || el.getAttribute(arguments[1]))";
  private static final String TO_BOOLEAN = ".map(attr => attr ? 'true' : null)";
  private static final String TO_STRING = ".map(attr => String(attr))";

  @Override
  public List<String> texts(Driver driver, List<WebElement> elements) {
    try {
      return requireNonNull(driver.executeJavaScript("return Array.from(arguments[0])" +
                                                     ".map(el => (el.innerText || el.textContent)" +
                                                     ".replace(/[\\u200b\\u200e\\u200f]/g, '')" +
                                                     ".trim())", elements));
    }
    catch (UnsupportedCommandException | JavascriptException cannotUseJs) {
      return textsOneByOne(elements);
    }
  }

  protected List<String> textsOneByOne(List<WebElement> elements) {
    return elements.stream()
      .map(element -> element.getText())
      .toList();
  }

  @Override
  public List<@Nullable String> attributes(Driver driver, List<WebElement> elements, String attributeName) {
    try {
      String convert = BOOLEAN_ATTRIBUTES.contains(attributeName) ? TO_BOOLEAN : TO_STRING;
      return requireNonNull(driver.executeJavaScript(JS_GET_ATTRIBUTES + convert, elements, attributeName));
    }
    catch (UnsupportedCommandException | JavascriptException cannotUseJs) {
      return attributesOneByOne(elements, attributeName);
    }
  }

  protected List<@Nullable String> attributesOneByOne(List<WebElement> elements, String attributeName) {
    return elements.stream()
      .map(element -> element.getAttribute(attributeName))
      .toList();
  }
}
