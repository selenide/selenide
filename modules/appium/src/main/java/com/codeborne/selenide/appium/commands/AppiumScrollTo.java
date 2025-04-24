package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.ScrollDirection;
import com.codeborne.selenide.commands.ScrollTo;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;
import java.util.Optional;

import static com.codeborne.selenide.appium.AppiumScrollOptions.with;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumScrollTo extends FluentCommand {
  private static final AppiumScrollOptions DEFAULT_OPTIONS = with(DOWN, 30);
  private final ScrollTo webImplementation = new ScrollTo();

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    Optional<AppiumDriver> driver = cast(locator.driver(), AppiumDriver.class);
    if (!driver.isPresent()) {
      scrollInWebBrowser(locator, args);
    }
    else {
      scrollInMobile(driver.get(), locator, args);
    }
  }

  private AppiumScrollOptions extractOptions(Object @Nullable [] args) {
    if (args == null || args.length == 0) {
      return DEFAULT_OPTIONS;
    } else if (args.length == 1) {
      return firstOf(args);
    } else {
      throw new IllegalArgumentException("Unsupported scroll arguments: " + Arrays.toString(args));
    }
  }

  private void scrollInWebBrowser(WebElementSource locator, Object @Nullable [] args) {
    if (new Arguments(args).ofType(AppiumScrollOptions.class).isPresent()) {
      throw new IllegalArgumentException("Scrolling with options is only supported for mobile drivers");
    }
    webImplementation.execute(locator, args);
  }

  private void scrollInMobile(AppiumDriver appiumDriver, WebElementSource locator, Object @Nullable [] args) {
    if (new Arguments(args).ofType(ScrollOptions.class).isPresent()) {
      throw new IllegalArgumentException("Use scroll(AppiumScrollOptions) instead of scroll(ScrollOptions)");
    }

    AppiumScrollOptions scrollOptions = extractOptions(args);
    int currentSwipeCount = 0;

    while (isElementNotDisplayed(locator)
           && isLessThanMaxSwipeCount(currentSwipeCount, scrollOptions.getMaxSwipeCount())) {
      performScroll(appiumDriver, scrollOptions.getScrollDirection(), scrollOptions.getTopPointHeightPercent(),
                    scrollOptions.getBottomPointHeightPercent());
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

  private Dimension getMobileDeviceSize(AppiumDriver appiumDriver) {
    return appiumDriver.manage().window().getSize();
  }

  private void performScroll(AppiumDriver appiumDriver, ScrollDirection scrollDirection, float top, float bottom) {
    Dimension size = getMobileDeviceSize(appiumDriver);
    AppiumScrollCoordinates scrollCoordinates = getScrollCoordinates(scrollDirection, size, top, bottom);
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence sequenceToPerformScroll = getSequenceToPerformScroll(finger, scrollCoordinates);
    appiumDriver.perform(singletonList(sequenceToPerformScroll));
  }

  private AppiumScrollCoordinates getScrollCoordinates(ScrollDirection scrollDirection, Dimension size, float top, float bottom) {
    if (scrollDirection == ScrollDirection.UP) {
      return new AppiumScrollCoordinates(size.getWidth() / 2, (int) (size.getHeight() * top),
                                         size.getWidth() / 2, (int) (size.getHeight() * bottom));
    } else {
      return new AppiumScrollCoordinates(size.getWidth() / 2, (int) (size.getHeight() * bottom),
                                         size.getWidth() / 2, (int) (size.getHeight() * top));
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

