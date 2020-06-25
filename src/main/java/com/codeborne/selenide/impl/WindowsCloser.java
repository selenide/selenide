package com.codeborne.selenide.impl;

import com.google.errorprone.annotations.CheckReturnValue;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
public class WindowsCloser {
  private static final Logger log = LoggerFactory.getLogger(WindowsCloser.class);

  @CheckReturnValue
  public <T> T runAndCloseArisedWindows(WebDriver webDriver, SupplierWithException<T> lambda) throws FileNotFoundException {
    String originalWindowHandle = webDriver.getWindowHandle();
    Set<String> windowsBefore = webDriver.getWindowHandles();

    try {
      return lambda.get();
    }
    finally {
      closeArisedWindows(webDriver, originalWindowHandle, windowsBefore);
    }
  }

  private void closeArisedWindows(WebDriver webDriver, String originalWindowHandle, Set<String> windowsBefore) {
    Set<String> newWindows = newWindows(webDriver, windowsBefore);
    if (!newWindows.isEmpty()) {
      closeWindows(webDriver, newWindows);
      webDriver.switchTo().window(originalWindowHandle);
    }
  }

  @CheckReturnValue
  @Nonnull
  private Set<String> newWindows(WebDriver webDriver, Set<String> windowsBefore) {
    Set<String> windowHandles = webDriver.getWindowHandles();

    Set<String> newWindows = new HashSet<>(windowHandles);
    newWindows.removeAll(windowsBefore);
    return newWindows;
  }

  private void closeWindows(WebDriver webDriver, Set<String> windows) {
    log.info("File has been opened in a new window, let's close {} new windows", windows.size());

    for (String newWindow : windows) {
      closeWindow(webDriver, newWindow);
    }
  }

  private void closeWindow(WebDriver webDriver, String window) {
    log.info("  Let's close {}", window);
    try {
      webDriver.switchTo().window(window);
      webDriver.close();
    }
    catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
      log.info("  Failed to close {}: {}", window, Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
    }
    catch (Exception e) {
      log.warn("  Failed to close {}", window, e);
    }
  }

  @FunctionalInterface
  interface SupplierWithException<T> {
    T get() throws FileNotFoundException;
  }
}
