package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import io.appium.java_client.clipboard.HasClipboard;

import java.util.Optional;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class AppiumClipboard implements Clipboard {
  private final Driver driver;
  private final Clipboard defaultClipboard;

  public AppiumClipboard(Driver driver) {
    this.driver = driver;
    defaultClipboard = new DefaultClipboard(driver);
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public Clipboard object() {
    return this;
  }

  @Override
  public String getText() {
    return getWebDriver()
      .map(HasClipboard::getClipboardText)
      .orElseGet(defaultClipboard::getText);
  }

  @Override
  public void setText(String text) {
    Optional<HasClipboard> mobileDriver = getWebDriver();
    if (mobileDriver.isPresent()) {
      mobileDriver.get().setClipboardText(text);
    }
    else {
      defaultClipboard.setText(text);
    }
  }

  private Optional<HasClipboard> getWebDriver() {
    return cast(driver, HasClipboard.class);
  }
}
