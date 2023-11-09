package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class WebElementCommunicator implements ElementCommunicator {
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
      return driver.executeJavaScript(
        "return Array.from(arguments[0]).map(el => el.getAttribute(arguments[1]))",
        elements, attributeName
      );
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
