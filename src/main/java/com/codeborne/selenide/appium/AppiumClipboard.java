package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import io.appium.java_client.clipboard.HasClipboard;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 * @since 1.6.10
 */
@ParametersAreNonnullByDefault
public class AppiumClipboard implements Clipboard {
  private final Driver driver;
  private final Clipboard defaultClipboard;

  public AppiumClipboard(Driver driver) {
    this.driver = driver;
    defaultClipboard = new DefaultClipboard(driver);
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
    WebDriver webdriver = driver.getWebDriver();
    if (webdriver instanceof WrapsDriver) {
      webdriver = ((WrapsDriver) webdriver).getWrappedDriver();
    }

    return webdriver instanceof HasClipboard ?
      Optional.of((HasClipboard) webdriver) :
      Optional.empty();
  }
}
