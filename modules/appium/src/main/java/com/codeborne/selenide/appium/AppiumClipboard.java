package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.Driver;
import io.appium.java_client.clipboard.HasClipboard;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @since 1.6.10
 */
@ParametersAreNonnullByDefault
public class AppiumClipboard implements Clipboard {
  private final Driver driver;

  public AppiumClipboard(Driver driver) {
    this.driver = driver;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public Driver driver() {
    return driver;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public Clipboard object() {
    return this;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public String getText() {
    return getWebDriver().getClipboardText();
  }

  @Override
  public void setText(String text) {
    getWebDriver().setClipboardText(text);
  }

  private HasClipboard getWebDriver() {
    return (HasClipboard) driver.getWebDriver();
  }
}
