package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByShadow;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.SelectorMode.CSS;
import static java.util.Objects.requireNonNull;

/**
 * Thanks to http://selenium.polteq.com/en/injecting-the-sizzle-css-selector-library/
 */
public class WebElementSelector {
  public static WebElementSelector instance = new WebElementSelector();

  protected final FileContent sizzleSource = new FileContent("sizzle.js");

  public WebElement findElement(Driver driver, @Nullable WebElementSource parent, By selector, int index) {
    return index == 0 ?
      findElement(driver, parent, selector) :
      findElements(driver, parent, selector).get(index);
  }

  public WebElement findElement(Driver driver, @Nullable WebElementSource parent, By selector) {
    SearchContext context = getSearchContext(driver, parent);
    checkThatXPathNotStartingFromSlash(context, selector);

    if (driver.config().selectorMode() == CSS || !(selector instanceof ByCssSelector)) {
      return findElement(context, selector);
    }

    List<WebElement> webElements = evaluateSizzleSelector(driver, context, (ByCssSelector) selector);
    if (webElements.isEmpty()) {
      throw new NoSuchElementException("Cannot locate an element using " + selector);
    }
    return webElements.get(0);
  }

  private static SearchContext getSearchContext(Driver driver, @Nullable WebElementSource parent) {
    if (parent == null) {
      return driver.getWebDriver();
    }

    WebElement parentElement = parent.getWebElement();
    return parent.isShadowRoot()
           ? ByShadow.getShadowRoot(parentElement)
             .orElseThrow(() -> new IllegalArgumentException(parentElement + " does not contain shadow root"))
           : parentElement;
  }

  public List<WebElement> findElements(Driver driver, @Nullable WebElementSource parent, By selector) {
    SearchContext context = getSearchContext(driver, parent);
    checkThatXPathNotStartingFromSlash(context, selector);

    if (driver.config().selectorMode() == CSS || !(selector instanceof ByCssSelector)) {
      return findElements(context, selector);
    }

    return evaluateSizzleSelector(driver, context, (ByCssSelector) selector);
  }

  private WebElement findElement(SearchContext context, By selector) {
    try {
      return context instanceof SelenideElement selenideElement ?
        selenideElement.toWebElement().findElement(selector) :
        context.findElement(selector);
    }
    catch (JavascriptException e) {
      throw unwrapInvalidSelectorException(e);
    }
  }

  private List<WebElement> findElements(SearchContext context, By selector) {
    try {
      return context instanceof SelenideElement selenideElement ?
        selenideElement.toWebElement().findElements(selector) :
        context.findElements(selector);
    }
    catch (JavascriptException e) {
      throw unwrapInvalidSelectorException(e);
    }
  }

  private static WebDriverException unwrapInvalidSelectorException(JavascriptException e) {
    return e.getMessage().contains("An invalid or illegal selector was specified") ||
           e.getMessage().contains("not a valid XPath expression") ?
      new InvalidSelectorException(e.getMessage(), e.getCause()) : e;
  }

  protected void checkThatXPathNotStartingFromSlash(SearchContext context, By selector) {
    if (context instanceof WebElement) {
      if (selector instanceof By.ByXPath) {
        if (selector.toString().startsWith("By.xpath: /")) {
          throw new IllegalArgumentException("XPath starting from / searches from root");
        }
      }
    }
  }

  protected List<WebElement> evaluateSizzleSelector(Driver driver, SearchContext context, ByCssSelector sizzleCssSelector) {
    injectSizzleIfNeeded(driver);

    String sizzleSelector = sizzleCssSelector.toString()
      .replace("By.selector: ", "")
      .replace("By.cssSelector: ", "");

    if (context instanceof WebElement)
      return requireNonNull(driver.executeJavaScript("return Sizzle(arguments[0], arguments[1])", sizzleSelector, context));
    else
      return requireNonNull(driver.executeJavaScript("return Sizzle(arguments[0])", sizzleSelector));
  }

  protected void injectSizzleIfNeeded(Driver driver) {
    if (!sizzleLoaded(driver)) {
      injectSizzle(driver);
    }
  }

  protected Boolean sizzleLoaded(Driver driver) {
    try {
      return requireNonNull(driver.executeJavaScript("return typeof Sizzle != 'undefined'"));
    }
    catch (WebDriverException e) {
      return false;
    }
  }

  protected synchronized void injectSizzle(Driver driver) {
    driver.executeJavaScript(sizzleSource.content());
  }
}
