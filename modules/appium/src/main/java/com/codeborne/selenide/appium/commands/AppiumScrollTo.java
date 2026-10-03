package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.ScrollDirection;
import com.codeborne.selenide.commands.ScrollTo;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.codeborne.selenide.appium.AppiumScrollOptions.with;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.commands.Util.firstOf;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumScrollTo extends FluentCommand {
  private static final AppiumScrollOptions DEFAULT_OPTIONS = with(DOWN, 30);
  private final ScrollTo webImplementation = new ScrollTo();

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    if (isMobile(locator.driver())) {
      scrollInMobile(locator.driver().getWebDriver(), locator, args);
    }
    else {
      scrollInWebBrowser(locator, args);
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

  private void scrollInMobile(WebDriver appiumDriver, WebElementSource locator, Object @Nullable [] args) {
    if (new Arguments(args).ofType(ScrollOptions.class).isPresent()) {
      throw new IllegalArgumentException("Use scroll(AppiumScrollOptions) instead of scroll(ScrollOptions)");
    }

    AppiumScrollOptions scrollOptions = extractOptions(args);
    int currentSwipeCount = 0;

    while (isElementNotDisplayed(locator)
           && isLessThanMaxSwipeCount(currentSwipeCount, scrollOptions.getMaxSwipeCount())) {
      performScroll(appiumDriver, scrollOptions);
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

  private void performScroll(WebDriver appiumDriver, AppiumScrollOptions scrollOptions) {
    Rectangle gestureArea = GestureArea.of(appiumDriver, scrollOptions.getContainer());
    AppiumScrollCoordinates scrollCoordinates = getScrollCoordinates(scrollOptions.getScrollDirection(), gestureArea,
      scrollOptions.getTopPointHeightPercent(), scrollOptions.getBottomPointHeightPercent());
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence sequenceToPerformScroll = getSequenceToPerformScroll(finger, scrollCoordinates);
    ((Interactive) appiumDriver).perform(singletonList(sequenceToPerformScroll));
  }

  private AppiumScrollCoordinates getScrollCoordinates(ScrollDirection scrollDirection, Rectangle area,
                                                       float topPointHeightPercent, float bottomPointHeightPercent) {
    int x = area.getX() + area.getWidth() / 2;
    int topY = area.getY() + (int) (area.getHeight() * topPointHeightPercent);
    int bottomY = area.getY() + (int) (area.getHeight() * bottomPointHeightPercent);
    return scrollDirection == ScrollDirection.UP ?
      new AppiumScrollCoordinates(x, topY, x, bottomY) :
      new AppiumScrollCoordinates(x, bottomY, x, topY);
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

