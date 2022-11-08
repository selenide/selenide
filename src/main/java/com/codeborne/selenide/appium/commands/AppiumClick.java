package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.AppiumClickOptions;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;
import static com.codeborne.selenide.commands.Util.firstOf;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

@ParametersAreNonnullByDefault
public class AppiumClick extends Click {

  @Override
  @Nullable
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement webElement = findElement(locator);

    if (args == null || args.length == 0) {
      click(locator.driver(), webElement);
    } else if (args.length == 1) {
      AppiumClickOptions appiumClickOptions = firstOf(args);
      click(locator.driver(), webElement, appiumClickOptions);
    } else {
      throw new IllegalArgumentException("Unsupported click arguments: " + Arrays.toString(args));
    }
    return proxy;
  }

  protected void click(Driver driver, WebElement webElement, AppiumClickOptions appiumClickOptions) {
    switch (appiumClickOptions.appiumClickMethod()) {
      case TAP_WITH_OFFSET: {
        performTapWithOffset(driver, webElement, appiumClickOptions.offsetX(), appiumClickOptions.offsetY());
        break;
      }
      case TAP: {
        performTapWithOffset(driver, webElement, 0, 0);
        break;
      }
      case DOUBLE_TAP: {
        performDoubleTap(driver, webElement);
        break;
      }
      case LONG_PRESS: {
        performLongPress(driver, webElement);
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown click option: " + appiumClickOptions.appiumClickMethod());
      }
    }
  }

  private void performLongPress(Driver driver, WebElement webElement) {
    new Actions(driver.getWebDriver()).clickAndHold(webElement).perform();
  }

  private void performDoubleTap(Driver driver, WebElement webElement) {
    Point size = webElement.getLocation();
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
    Sequence doubleTapSequence = getSequenceToPerformTap(finger, size, 0, 0)
      .addAction(new Pause(finger, ofMillis(40)))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
      .addAction(new Pause(finger, ofMillis(200)))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    perform(driver, doubleTapSequence);
  }

  private void performTapWithOffset(Driver driver, WebElement webElement, int offsetX, int offsetY) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
    Point size = getCenter(webElement);

    Sequence tapSequence = getSequenceToPerformTap(finger, size, offsetX, offsetY);
    perform(driver, tapSequence);
  }

  private void perform(Driver driver, Sequence sequence) {
    if (instanceOf(driver, AppiumDriver.class)) {
      ((AppiumDriver) driver.getWebDriver()).perform(singletonList(sequence));
    }
  }

  private Sequence getSequenceToPerformTap(PointerInput finger, Point size, int offsetX, int offsetY) {
    return new Sequence(finger, 1)
      .addAction(finger.createPointerMove(ofMillis(0),
        PointerInput.Origin.viewport(), size.getX() + offsetX, size.getY() + offsetY))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
      .addAction(new Pause(finger, ofMillis(200)))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
  }

  public Point getCenter(WebElement element) {
    int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
    int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
    return new Point(x, y);
  }

  @Override
  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    if (instanceOf(locator.driver(), AppiumDriver.class)) {
      return locator.getWebElement();
    } else {
      return super.findElement(locator);
    }
  }
}
