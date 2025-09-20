package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumSwipeDirection;
import com.codeborne.selenide.appium.AppiumSwipeOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;
import static com.codeborne.selenide.appium.AppiumSwipeOptions.right;
import static com.codeborne.selenide.commands.Util.firstOf;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumSwipeTo extends FluentCommand {
  private static final AppiumSwipeOptions DEFAULT_OPTIONS = right();

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    AppiumSwipeOptions appiumSwipeOptions = extractOptions(args);

    if (!isMobile(locator.driver())) {
      throw new IllegalArgumentException("Swipe is supported only in Appium");
    }
    swipeInMobile(locator, appiumSwipeOptions);
  }

  private void swipeInMobile(WebElementSource locator, AppiumSwipeOptions appiumSwipeOptions) {
    WebDriver appiumDriver = locator.driver().getWebDriver();

    int currentSwipeCount = 0;
    while (isElementNotDisplayed(locator)
      && isLessThanMaxSwipeCount(currentSwipeCount, appiumSwipeOptions.getMaxSwipeCounts())) {
      performSwipe(appiumDriver, appiumSwipeOptions.getAppiumSwipeDirection());
      currentSwipeCount++;
    }
  }

  private boolean isLessThanMaxSwipeCount(int currentSwipeCount, int maxSwipeCounts) {
    return currentSwipeCount < maxSwipeCounts;
  }

  private boolean isElementNotDisplayed(WebElementSource locator) {
    try {
      return !locator.getWebElement().isDisplayed();
    } catch (NoSuchElementException noSuchElementException) {
      return true;
    }
  }

  private Dimension getMobileDeviceSize(WebDriver appiumDriver) {
    return appiumDriver.manage().window().getSize();
  }

  private void performSwipe(WebDriver appiumDriver, AppiumSwipeDirection swipeDirection) {
    Dimension size = getMobileDeviceSize(appiumDriver);
    AppiumScrollCoordinates scrollCoordinates = getScrollCoordinates(swipeDirection, size);
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence sequenceToPerformScroll = getSequenceToPerformSwipe(finger, scrollCoordinates);
    ((Interactive) appiumDriver).perform(singletonList(sequenceToPerformScroll));
  }

  private AppiumScrollCoordinates getScrollCoordinates(AppiumSwipeDirection swipeDirection, Dimension size) {
    if (swipeDirection == RIGHT) {
      return new AppiumScrollCoordinates((int) (size.getWidth() * 0.75), size.getHeight() / 2,
        (int) (size.getWidth() * 0.25), size.getHeight() / 2);
    } else {
      return new AppiumScrollCoordinates((int) (size.getWidth() * 0.25), size.getHeight() / 2,
        (int) (size.getWidth() * 0.75), size.getHeight() / 2);
    }
  }

  private Sequence getSequenceToPerformSwipe(PointerInput finger, AppiumScrollCoordinates scrollCoordinates) {
    return new Sequence(finger, 1)
      .addAction(finger.createPointerMove(ofMillis(0),
        PointerInput.Origin.viewport(), scrollCoordinates.getStartX(), scrollCoordinates.getStartY()))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()))
      .addAction(finger.createPointerMove(ofMillis(200),
        PointerInput.Origin.viewport(), scrollCoordinates.getEndX(), scrollCoordinates.getEndY()))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
  }

  private AppiumSwipeOptions extractOptions(Object @Nullable [] args) {
    if (args == null || args.length == 0) {
      return DEFAULT_OPTIONS;
    } else if (args.length == 1) {
      return firstOf(args);
    } else {
      throw new IllegalArgumentException("Unsupported swipe arguments: " + Arrays.toString(args));
    }
  }
}
