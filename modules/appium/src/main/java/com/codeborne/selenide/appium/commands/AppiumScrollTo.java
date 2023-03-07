package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.ScrollDirection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;
import static com.codeborne.selenide.commands.Util.firstOf;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumScrollTo implements Command<SelenideAppiumElement> {

  @Override
  @Nonnull
  public SelenideAppiumElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    AppiumDriver appiumDriver = cast(locator.driver(), AppiumDriver.class)
      .orElseThrow(() -> new IllegalStateException("Driver should be either an instance of Android or Ios Driver"));

    AppiumScrollOptions appiumScrollOptions;

    if (args == null || args.length == 0) {
      appiumScrollOptions = AppiumScrollOptions.with(ScrollDirection.DOWN, 30);
    } else if (args.length == 1) {
      appiumScrollOptions = firstOf(args);
    } else {
      throw new IllegalArgumentException("Unsupported scroll arguments: " + Arrays.toString(args));
    }
    int currentSwipeCount = 0;
    String previousPageSource = "";

    while (isElementNotDisplayed(locator)
      && isNotEndOfPage(appiumDriver, previousPageSource)
      && isLessThanMaxSwipeCount(currentSwipeCount, appiumScrollOptions.getMaxSwipeCounts())) {
      previousPageSource = appiumDriver.getPageSource();
      performScroll(appiumDriver, appiumScrollOptions);
      currentSwipeCount++;
    }
    return (SelenideAppiumElement) proxy;
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

  private boolean isNotEndOfPage(AppiumDriver appiumDriver, String initialPageSource) {
    return !initialPageSource.equals(appiumDriver.getPageSource());
  }

  private Dimension getMobileDeviceSize(AppiumDriver appiumDriver) {
    return appiumDriver.manage().window().getSize();
  }

  private void performScroll(AppiumDriver appiumDriver, AppiumScrollOptions scrollOptions) {
    Dimension size = getMobileDeviceSize(appiumDriver);
    AppiumScrollCoordinates scrollCoordinates = getScrollCoordinates(scrollOptions, size);
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence sequenceToPerformScroll = getSequenceToPerformScroll(finger, scrollCoordinates);
    appiumDriver.perform(singletonList(sequenceToPerformScroll));
  }

  private AppiumScrollCoordinates getScrollCoordinates(AppiumScrollOptions scrollOptions, Dimension size) {
    if (scrollOptions.getScrollDirection() == ScrollDirection.UP) {
      return new AppiumScrollCoordinates(size.getWidth() / 2, (int) (size.getHeight() * 0.25), size.getWidth() / 2, size.getHeight() / 2);
    } else {
      return new AppiumScrollCoordinates(size.getWidth() / 2, size.getHeight() / 2, size.getWidth() / 2, (int) (size.getHeight() * 0.25));
    }
  }

  private Sequence getSequenceToPerformScroll(PointerInput finger, AppiumScrollCoordinates scrollCoordinates) {
    return new Sequence(finger, 1)
      .addAction(finger.createPointerMove(ofMillis(0),
                                          PointerInput.Origin.viewport(), scrollCoordinates.getStartX(), scrollCoordinates.getStartY()))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()))
      .addAction(finger.createPointerMove(ofMillis(200),
                                          PointerInput.Origin.viewport(), scrollCoordinates.getEndX(), scrollCoordinates.getEndY()))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
  }
}

