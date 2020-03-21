package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Describe {
  private static final Logger log = LoggerFactory.getLogger(Describe.class);

  private final Driver driver;
  private final WebElement element;
  private final StringBuilder sb = new StringBuilder();

  private Describe(Driver driver, WebElement element) {
    this.driver = driver;
    this.element = element;
    sb.append('<').append(element.getTagName());
  }

  private Describe appendAttributes() {
    try {
      if (supportsJavascriptAttributes()) {
        return appendAllAttributes();
      }
    }
    catch (NoSuchElementException | UnsupportedOperationException | UnsupportedCommandException |
      StaleElementReferenceException browserDoesNotSupportJavaScript) {
    }
    catch (WebDriverException probablyBrowserDoesNotSupportJavaScript) {
      if (!probablyBrowserDoesNotSupportJavaScript.getMessage().toLowerCase().contains("method is not implemented")) {
        log.warn("Failed to get attributes via JS: {}", probablyBrowserDoesNotSupportJavaScript.toString());
      }
    }
    return appendPredefinedAttributes();
  }

  private Describe appendAllAttributes() {
    Map<String, String> map = driver.executeJavaScript(
        "var s = {};" +
            "var attrs = arguments[0].attributes;" +
            "for (var i = 0; i < attrs.length; i++) {" +
            "   var a = attrs[i]; " +
            "   if (a.name != 'style') {" +
            "     s[a.name] = a.value;" +
            "   }" +
            "}" +
            "return s;", element);

    SortedMap<String, String> sortedByName = new TreeMap<>();
    if (map != null) {
      sortedByName.putAll(map);
    }
    sortedByName.put("value", element.getAttribute("value"));
    if (!sortedByName.containsKey("type")) {
      sortedByName.put("type", element.getAttribute("type"));
    }

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

  private Describe attr(String attributeName) {
    try {
      String attributeValue = element.getAttribute(attributeName);
      return attr(attributeName, attributeValue);
    }
    catch (NoSuchElementException | UnsupportedOperationException | UnsupportedCommandException |
      StaleElementReferenceException browserDoesNotSupportJavaScript) {
      return this;
    }
    catch (WebDriverException probablyBrowserDoesNotSupportJavaScript) {
      if (!probablyBrowserDoesNotSupportJavaScript.getMessage().toLowerCase().contains("method is not implemented")) {
        log.warn("Failed to get attribute {}: {}", attributeName, probablyBrowserDoesNotSupportJavaScript.toString());
      }
      return this;
    }
  }

  private Describe attr(String attributeName, String attributeValue) {
    if (attributeValue != null) {
      if (attributeValue.length() > 0) {
        sb.append(' ').append(attributeName).append("=\"").append(attributeValue).append('"');
      } else {
        sb.append(' ').append(attributeName);
      }
    }
    return this;
  }

  private String serialize() {
    String text = element.getText();
    sb.append('>').append(text == null ? "" : text).append("</").append(element.getTagName()).append('>');
    return sb.toString();
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  private String flush() {
    return sb.append('>').toString();
  }

  public static String describe(Driver driver, WebElement element) {
    try {
      if (element == null) {
        return "null";
      }
      return new Describe(driver, element)
          .appendAttributes()
          .isSelected(element)
          .isDisplayed(element)
          .serialize();
    } catch (WebDriverException elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    }
    catch (IndexOutOfBoundsException e) {
      return e.toString();
    }
  }

  static String shortly(Driver driver, WebElement element) {
    try {
      if (element == null) {
        return "null";
      }
      if (element instanceof SelenideElement) {
        return shortly(driver, ((SelenideElement) element).toWebElement());
      }
      return new Describe(driver, element).attr("id").attr("name").flush();
    } catch (WebDriverException elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    }
    catch (IndexOutOfBoundsException e) {
      return e.toString();
    }
  }

  private Describe isSelected(WebElement element) {
    try {
      if (element.isSelected()) {
        sb.append(' ').append("selected:true");
      }
    } catch (UnsupportedOperationException | WebDriverException ignore) {
    }
    return this;
  }

  private Describe isDisplayed(WebElement element) {
    try {
      if (!element.isDisplayed()) {
        sb.append(' ').append("displayed:false");
      }
    } catch (UnsupportedOperationException | WebDriverException e) {
      sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e));
    }
    return this;
  }

  static String shortly(By selector) {
    if (selector instanceof By.ByCssSelector) {
      return selector.toString()
          .replaceFirst("By\\.selector:\\s*(.*)", "$1")
          .replaceFirst("By\\.cssSelector:\\s*(.*)", "$1");
    }
    return selector.toString();
  }

  public static String selector(By selector) {
    return selector.toString()
        .replaceFirst("By\\.selector:\\s*", "")
        .replaceFirst("By\\.cssSelector:\\s*", "");
  }
}
