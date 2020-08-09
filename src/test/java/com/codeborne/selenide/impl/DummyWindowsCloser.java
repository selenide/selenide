package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class DummyWindowsCloser extends WindowsCloser {
  @Override
  public <T> T runAndCloseArisedWindows(WebDriver webDriver, SupplierWithException<T> lambda) throws IOException {
    return lambda.get();
  }
}
