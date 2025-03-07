package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Describe {
  private static final Logger log = LoggerFactory.getLogger(Describe.class);

  private final JavaScript js = new JavaScript("get-element-attributes.js");
  private final Driver driver;
  private final WebElement element;
  private final StringBuilder sb = new StringBuilder();

  public Describe(Driver driver, WebElement element) {
    this.driver = driver;
    this.element = element;
    sb.append('<').append(element.getTagName());
  }

  public Describe appendAttributes() {
    try {
      if (supportsJavascriptAttributes()) {
        return appendAllAttributes();
      }
    }
    catch (NoSuchElementException | UnsupportedOperationException | UnsupportedCommandException |
      StaleElementReferenceException browserDoesNotSupportJavaScript) {
      // ignore
    }
    catch (WebDriverException probablyBrowserDoesNotSupportJavaScript) {
      if (!probablyBrowserDoesNotSupportJavaScript.getMessage().toLowerCase(Locale.ROOT).contains("method is not implemented")) {
        log.warn("Failed to get attributes via JS: {}", probablyBrowserDoesNotSupportJavaScript.toString());
      }
    }
    return appendPredefinedAttributes();
  }

  private Describe appendAllAttributes() {
    Map<String, String> map = requireNonNull(js.execute(driver, element));
    SortedMap<String, String> sortedByName = new TreeMap<>(map);

    for (Map.Entry<String, String> entry : sortedByName.entrySet()) {
      attr(entry.getKey(), entry.getValue());
    }
    return this;
  }

  private Describe appendPredefinedAttributes() {
    return attr("class").attr("disabled").attr("readonly").attr("href").attr("id").attr("name")
      .attr("onclick").attr("onchange").attr("placeholder")
      .attr("type").attr("value");
  }

  private boolean supportsJavascriptAttributes() {
    return driver.supportsJavascript();
  }

  public Describe attr(String attributeName) {
    try {
      String attributeValue = element.getAttribute(attributeName);
      return attr(attributeName, attributeValue);
    }
    catch (NoSuchElementException | UnsupportedOperationException | UnsupportedCommandException |
      StaleElementReferenceException browserDoesNotSupportJavaScript) {
      return this;
    }
    catch (WebDriverException probablyBrowserDoesNotSupportJavaScript) {
      if (!probablyBrowserDoesNotSupportJavaScript.getMessage().toLowerCase(Locale.ROOT).contains("method is not implemented")) {
        log.warn("Failed to get attribute {}: {}", attributeName, probablyBrowserDoesNotSupportJavaScript.toString());
      }
      return this;
    }
  }

  @CanIgnoreReturnValue
  private Describe attr(String attributeName, @Nullable String attributeValue) {
    if (attributeValue != null) {
      if (!attributeValue.isEmpty()) {
        sb.append(' ').append(attributeName).append("=\"").append(attributeValue).append('"');
      }
      else {
        sb.append(' ').append(attributeName);
      }
    }
    return this;
  }

  @SuppressWarnings("ConstantValue")
  public String serialize() {
    String text = safeCall("text", element::getText);
    sb.append('>').append(text == null ? "" : text).append("</").append(safeCall("tagName", element::getTagName)).append('>');
    return sb.toString();
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  public String flush() {
    return sb.append('>').toString();
  }

  public Describe isSelected(WebElement element) {
    try {
      if (element.isSelected()) {
        sb.append(' ').append("selected:true");
      }
    }
    catch (UnsupportedOperationException | WebDriverException ignore) {
    }
    return this;
  }

  public Describe isDisplayed(WebElement element) {
    try {
      if (!element.isDisplayed()) {
        sb.append(' ').append("displayed:false");
      }
    }
    catch (UnsupportedOperationException | WebDriverException e) {
      log.debug("Failed to check visibility", e);
      sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e));
    }
    catch (RuntimeException e) {
      log.error("Failed to check visibility", e);
      sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e));
    }
    return this;
  }

  private String safeCall(String name, Supplier<String> method) {
    try {
      return method.get();
    }
    catch (WebDriverException e) {
      log.debug("Failed to get {}", name, e);
      return Cleanup.of.webdriverExceptionMessage(e);
    }
    catch (RuntimeException e) {
      log.error("Failed to get {}", name, e);
      return "?";
    }
  }
}
