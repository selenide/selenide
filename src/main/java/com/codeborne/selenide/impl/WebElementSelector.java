package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.SelectorMode.CSS;

/**
 * Thanks to http://selenium.polteq.com/en/injecting-the-sizzle-css-selector-library/
 */
@ParametersAreNonnullByDefault
public class WebElementSelector {
  public static WebElementSelector instance = new WebElementSelector();

  protected final FileContent sizzleSource = new FileContent("sizzle.js");

  @CheckReturnValue
  @Nonnull
  public WebElement findElement(Driver driver, SearchContext context, By selector) {
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

  @CheckReturnValue
  @Nonnull
  public List<WebElement> findElements(Driver driver, SearchContext context, By selector) {
    checkThatXPathNotStartingFromSlash(context, selector);

    if (driver.config().selectorMode() == CSS || !(selector instanceof ByCssSelector)) {
      return findElements(context, selector);
    }

    return evaluateSizzleSelector(driver, context, (ByCssSelector) selector);
  }

  private WebElement findElement(SearchContext context, By selector) {
    return context instanceof SelenideElement ?
      ((SelenideElement) context).toWebElement().findElement(selector) :
      context.findElement(selector);
  }

  private List<WebElement> findElements(SearchContext context, By selector) {
    return context instanceof SelenideElement ?
      ((SelenideElement) context).toWebElement().findElements(selector) :
      context.findElements(selector);
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

  @CheckReturnValue
  @Nonnull
  protected List<WebElement> evaluateSizzleSelector(Driver driver, SearchContext context, ByCssSelector sizzleCssSelector) {
    injectSizzleIfNeeded(driver);

    String sizzleSelector = sizzleCssSelector.toString()
        .replace("By.selector: ", "")
        .replace("By.cssSelector: ", "");

    if (context instanceof WebElement)
      return driver.executeJavaScript("return Sizzle(arguments[0], arguments[1])", sizzleSelector, context);
    else
      return driver.executeJavaScript("return Sizzle(arguments[0])", sizzleSelector);
  }

  protected void injectSizzleIfNeeded(Driver driver) {
    if (!sizzleLoaded(driver)) {
      injectSizzle(driver);
    }
  }

  protected Boolean sizzleLoaded(Driver driver) {
    try {
      return driver.executeJavaScript("return typeof Sizzle != 'undefined'");
    } catch (WebDriverException e) {
      return false;
    }
  }

  protected synchronized void injectSizzle(Driver driver) {
    driver.executeJavaScript(sizzleSource.content());
  }
}
