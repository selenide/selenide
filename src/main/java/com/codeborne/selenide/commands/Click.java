package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickMethod;
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

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.commands.Util.size;

@ParametersAreNonnullByDefault
public class Click implements Command<SelenideElement> {
  private final JavaScript jsSource = new JavaScript("click.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    ClickOptions clickOptions = options(args);
    click(locator.driver(), findElement(locator, clickOptions.isForce()), clickOptions);
    return proxy;
  }

  @Nonnull
  @CheckReturnValue
  protected ClickOptions options(@Nullable Object[] args) {
    return switch (size(args)) {
      case 0 -> usingDefaultMethod();
      case 1 -> firstOf(args);
      default -> throw new IllegalArgumentException("Unsupported click arguments: " + Arrays.toString(args));
    };
  }

  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator, boolean force) {
    return force ?
      locator.getWebElement() :
      locator.findAndAssertElementIsClickable();
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
    ClickMethod method = detectMethod(driver, clickOptions);

    switch (method) {
      case DEFAULT: {
        defaultClick(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      case JS: {
        clickViaJS(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown click option: " + method);
      }
    }
  }

  @Nonnull
  private ClickMethod detectMethod(Driver driver, ClickOptions clickOptions) {
    ClickMethod method = clickOptions.clickMethod();
    return method == DEFAULT && driver.config().clickViaJs() ?
      JS :
      method;
  }

  protected void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY) {
    if (isCenter(offsetX, offsetY)) {
      element.click();
    }
    else {
      driver.actions()
        .moveToElement(element, offsetX, offsetY)
        .click()
        .perform();
    }
  }

  protected boolean isCenter(int offsetX, int offsetY) {
    return offsetX == 0 && offsetY == 0;
  }

  protected void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY) {
    jsSource.execute(driver, element, offsetX, offsetY);
  }
}
