package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import io.appium.java_client.clipboard.HasClipboard;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

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
    WebDriver webDriver = driver.getWebDriver();
    if (isMobile(webDriver)) {
      return ((HasClipboard) webDriver).getClipboardText();
    }
    else {
      return defaultClipboard.getText();
    }
  }

  @Override
  public void setText(String text) {
    WebDriver webDriver = driver.getWebDriver();
    if (isMobile(webDriver)) {
      ((HasClipboard) webDriver).setClipboardText(text);
    }
    else {
      defaultClipboard.setText(text);
    }
  }
}
