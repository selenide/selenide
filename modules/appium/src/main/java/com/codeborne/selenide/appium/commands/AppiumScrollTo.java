package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.ScrollDirection;
import com.codeborne.selenide.commands.ScrollTo;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;

import static com.codeborne.selenide.appium.AppiumScrollOptions.with;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;
import static com.codeborne.selenide.commands.Util.firstOf;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

@ParametersAreNonnullByDefault
public class AppiumScrollTo implements Command<SelenideElement> {
  private static final AppiumScrollOptions DEFAULT_OPTIONS = with(DOWN, 30);
  private final ScrollTo webImplementation = new ScrollTo();

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    AppiumScrollOptions scrollOptions = extractOptions(args);

    Optional<AppiumDriver> driver = cast(locator.driver(), AppiumDriver.class);
    if (!driver.isPresent()) {
      scrollInWebBrowser(proxy, locator, args, scrollOptions);
    }
    else {
      scrollInMobile(driver.get(), locator, scrollOptions);
    }
    return proxy;
  }

  @Nonnull
  @CheckReturnValue
  private AppiumScrollOptions extractOptions(@Nullable Object[] args) {
    if (args == null || args.length == 0) {
      return DEFAULT_OPTIONS;
    } else if (args.length == 1) {
      return firstOf(args);
    } else {
      throw new IllegalArgumentException("Unsupported scroll arguments: " + Arrays.toString(args));
    }
  }

  private void scrollInWebBrowser(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args,
                                             AppiumScrollOptions appiumScrollOptions) {
    if (appiumScrollOptions != DEFAULT_OPTIONS) {
      throw new IllegalArgumentException("Scrolling with options is only supported for mobile drivers");
    }
    webImplementation.execute(proxy, locator, args);
  }

  private void scrollInMobile(AppiumDriver appiumDriver, WebElementSource locator, AppiumScrollOptions scrollOptions) {
    int currentSwipeCount = 0;

    while (isElementNotDisplayed(locator)
           && isLessThanMaxSwipeCount(currentSwipeCount, scrollOptions.getMaxSwipeCounts())) {
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

