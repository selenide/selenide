package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class DummyWindowsCloser extends WindowsCloser {
  @Override
  public <T> T runAndCloseArisedWindows(WebDriver webDriver, Supplier<T> lambda) {
    return lambda.get();
  }
}
