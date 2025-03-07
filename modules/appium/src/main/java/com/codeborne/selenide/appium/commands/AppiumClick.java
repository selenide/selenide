package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.appium.AppiumClickOptions;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumClick extends Click {

  private static final String FINGER_1 = "finger1";

  @Override
  public void execute(WebElementSource locator, Object @Nullable [] args) {
    if (!isMobile(locator.driver())) {
      super.execute(locator, args);
      return;
    }

    ClickOptions options = options(args);
    WebElement webElement = findElement(locator, options.isForce());

    if (options instanceof AppiumClickOptions appiumClickOptions) {
      click(locator.driver(), webElement, appiumClickOptions);
    }
    else {
      click(locator.driver(), webElement, options);
    }
  }

  @Override
  protected void click(Driver driver, WebElement webElement, ClickOptions options) {
    if (isMobile(driver)) {
      if (options.timeout() != null) {
        throw new UnsupportedOperationException("Click timeout is not supported in mobile");
      }
      if (options.clickMethod() == JS || driver.config().clickViaJs()) {
        throw new UnsupportedOperationException("Click using JavaScript is not supported in mobile");
      }
    }
    super.click(driver, webElement, options);
  }

  protected void click(Driver driver, WebElement webElement, AppiumClickOptions appiumClickOptions) {
    switch (appiumClickOptions.appiumClickMethod()) {
      case TAP_WITH_OFFSET -> performTapWithOffset(driver, webElement, appiumClickOptions.offsetX(), appiumClickOptions.offsetY());
      case TAP -> performTapWithOffset(driver, webElement, 0, 0);
      case DOUBLE_TAP -> performDoubleTap(driver, webElement);
      case LONG_PRESS -> performLongPress(driver, webElement, appiumClickOptions);
      default -> throw new IllegalArgumentException("Unknown click option: " + appiumClickOptions.appiumClickMethod());
    }
  }

  private void performDoubleTap(Driver driver, WebElement webElement) {
    Point size = webElement.getLocation();
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, FINGER_1);
    Sequence doubleTapSequence = getSequenceToPerformTap(finger, size, 0, 0)
      .addAction(new Pause(finger, ofMillis(40)))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
      .addAction(new Pause(finger, ofMillis(200)))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    perform(driver, doubleTapSequence);
  }

  private void performLongPress(Driver driver, WebElement webElement, AppiumClickOptions appiumClickOptions) {
    Point size = webElement.getLocation();
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, FINGER_1);
    Sequence doubleTapSequence = new Sequence(finger, 1)
      .addAction(finger.createPointerMove(ofMillis(0),
        PointerInput.Origin.viewport(), size.getX(), size.getY()))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
      .addAction(new Pause(finger, appiumClickOptions.longPressHoldDuration()))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    perform(driver, doubleTapSequence);
  }

  private void performTapWithOffset(Driver driver, WebElement webElement, int offsetX, int offsetY) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, FINGER_1);
    Point size = getCenter(webElement);

    Sequence tapSequence = getSequenceToPerformTap(finger, size, offsetX, offsetY);
    perform(driver, tapSequence);
  }

  private void perform(Driver driver, Sequence sequence) {
    WebDriver webDriver = driver.getWebDriver();
    AppiumDriver appiumDriver = cast(webDriver, AppiumDriver.class)
      .orElseThrow(() -> new IllegalStateException("Not a mobile webdriver: " + webDriver));
    appiumDriver.perform(singletonList(sequence));
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
  protected WebElement findElement(WebElementSource locator, boolean force) {
    if (isMobile(locator.driver())) {
      return force ? locator.getWebElement() : locator.findAndAssertElementIsVisible();
    } else {
      return super.findElement(locator, force);
    }
  }
}
