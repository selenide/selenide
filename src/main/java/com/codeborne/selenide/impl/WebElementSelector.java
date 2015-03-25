package com.codeborne.selenide.impl;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Configuration.SelectorMode.CSS;
import static com.codeborne.selenide.Configuration.selectorMode;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.lang.Thread.currentThread;

/**
 * Thanks to http://selenium.polteq.com/en/injecting-the-sizzle-css-selector-library/
 */
public class WebElementSelector {
  public static WebElementSelector instance = new WebElementSelector();
  
  protected String sizzleSource;

  public WebElement findElement(SearchContext context, By selector) {
    if (selectorMode == CSS || !(selector instanceof ByCssSelector)) {
      return context.findElement(selector);
    }
    
    List<WebElement> webElements = evaluateSizzleSelector(context, (ByCssSelector) selector);
    return webElements.isEmpty() ? null : webElements.get(0);
  }

  public List<WebElement> findElements(SearchContext context, By selector) {
    if (selectorMode == CSS || !(selector instanceof ByCssSelector)) {
      return context.findElements(selector);
    }
    
    return evaluateSizzleSelector(context, (ByCssSelector) selector);
  }

  protected List<WebElement> evaluateSizzleSelector(SearchContext context, ByCssSelector sizzleCssSelector) {
    injectSizzleIfNeeded();

    String sizzleSelector = sizzleCssSelector.toString()
        .replace("By.selector: ", "")
        .replace("By.cssSelector: ", "");
    
    if (context instanceof WebElement)
      return executeJavaScript("return Sizzle(arguments[0], arguments[1])", sizzleSelector, context);
    else
      return executeJavaScript("return Sizzle(arguments[0])", sizzleSelector);
  }

  protected void injectSizzleIfNeeded() {
    if (!sizzleLoaded()) {
      injectSizzle();
    }
  }

  protected Boolean sizzleLoaded() {
    try {
      return executeJavaScript("return typeof Sizzle != 'undefined'");
    } catch (WebDriverException e) {
      return false;
    }
  }
  
  protected synchronized void injectSizzle() {
    if (sizzleSource == null) {
      try {
        sizzleSource = IOUtils.toString(currentThread().getContextClassLoader().getResource("sizzle.js"));
      } catch (IOException e) {
        throw new RuntimeException("Cannot load sizzle.js from classpath", e);
      }
    }
    executeJavaScript(sizzleSource);
  }
}
