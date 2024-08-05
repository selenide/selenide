package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class WebElementCommunicator implements ElementCommunicator {
  /**
   * Taken from
   * <a href="https://selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html#getAttribute(java.lang.String)">spec</a>
   */
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
      return driver.executeJavaScript("return Array.from(arguments[0])" +
                                      ".map(el => (el.innerText || el.textContent)" +
                                      ".replace(/[\\u200b\\u200e\\u200f]/g, '')" +
                                      ".trim())", elements);
    }
    catch (UnsupportedCommandException | JavascriptException cannotUseJs) {
      return textsOneByOne(elements);
    }
  }

  @Nonnull
  @CheckReturnValue
  protected List<String> textsOneByOne(List<WebElement> elements) {
    return elements.stream()
      .map(element -> element.getText())
      .collect(Collectors.toList());
  }

  @Override
  public List<String> attributes(Driver driver, List<WebElement> elements, String attributeName) {
    try {
      String convert = BOOLEAN_ATTRIBUTES.contains(attributeName) ? TO_BOOLEAN : TO_STRING;
      return driver.executeJavaScript(JS_GET_ATTRIBUTES + convert, elements, attributeName);
    }
    catch (UnsupportedCommandException | JavascriptException cannotUseJs) {
      return attributesOneByOne(elements, attributeName);
    }
  }

  @Nonnull
  @CheckReturnValue
  protected List<String> attributesOneByOne(List<WebElement> elements, String attributeName) {
    return elements.stream()
      .map(element -> element.getAttribute(attributeName))
      .collect(Collectors.toList());
  }
}
