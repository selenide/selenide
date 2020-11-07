package com.codeborne.selenide.impl.windows;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

/**
 * A slightly fixed implementation of {@link ExpectedConditions#frameToBeAvailableAndSwitchToIt(org.openqa.selenium.By)}
 */
@ParametersAreNonnullByDefault
public class FrameByIdOrName implements ExpectedCondition<WebDriver> {
  private final By locator;

  public FrameByIdOrName(String frame) {
    locator = By.cssSelector(String.format("frame#%1$s,frame[name=%1$s],iframe#%1$s,iframe[name=%1$s]", frame));
  }

  @Override
  @Nullable
  public WebDriver apply(@SuppressWarnings("NullableProblems") WebDriver driver) {
    try {
      return requireNonNull(driver).switchTo().frame(driver.findElement(locator));
    } catch (WebDriverException e) {
      return null;
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "frame to be available: " + locator;
  }
}
