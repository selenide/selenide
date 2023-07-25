package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Arrays;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Click implements Command<SelenideElement> {
  private final JavaScript jsSource = new JavaScript("click.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement webElement = findElement(locator);

    if (args == null || args.length == 0) {
      click(locator.driver(), webElement);
    }
    else if (args.length == 1) {
      ClickOptions clickOptions = firstOf(args);
      click(locator.driver(), webElement, clickOptions);
    }
    else {
      throw new IllegalArgumentException("Unsupported click arguments: " + Arrays.toString(args));
    }
    return proxy;
  }

  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    return locator.findAndAssertElementIsClickable();
  }

  protected void click(Driver driver, WebElement element) {
    if (driver.config().clickViaJs()) {
      clickViaJS(driver, element, 0, 0);
    }
    else {
      defaultClick(driver, element);
    }
  }

  protected void click(Driver driver, WebElement webElement, ClickOptions clickOptions) {
    Duration timeout = clickOptions.timeout();
    if (timeout == null || timeout.toMillis() == driver.config().pageLoadTimeout()) {
      doClick(driver, webElement, clickOptions);
    }
    else {
      withTimeout(driver, timeout, () -> doClick(driver, webElement, clickOptions));
    }
  }

  private void withTimeout(Driver driver, Duration timeout, Runnable lambda) {
    WebDriver.Timeouts wdTimeouts = driver.getWebDriver().manage().timeouts();
    Duration originalPageLoadTimeout = wdTimeouts.getPageLoadTimeout();
    try {
      wdTimeouts.pageLoadTimeout(timeout);
      lambda.run();
    }
    finally {
      wdTimeouts.pageLoadTimeout(originalPageLoadTimeout);
    }
  }

  private void doClick(Driver driver, WebElement webElement, ClickOptions clickOptions) {
    switch (clickOptions.clickMethod()) {
      case DEFAULT: {
        defaultClick(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      case JS: {
        clickViaJS(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown click option: " + clickOptions.clickMethod());
      }
    }
  }

  protected void defaultClick(Driver driver, WebElement element) {
    element.click();
  }

  protected void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY) {
    driver.actions()
      .moveToElement(element, offsetX, offsetY)
      .click()
      .perform();
  }

  protected void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY) {
    jsSource.execute(driver, element, offsetX, offsetY);
  }
}
