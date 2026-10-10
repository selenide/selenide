package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.clipboard.HasClipboard;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.Base64;
import java.util.Map;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static io.appium.java_client.CommandExecutionHelper.execute;
import static io.appium.java_client.http.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNullElse;

public class AppiumClipboard implements Clipboard {
  private static final String PLAINTEXT = "plaintext";
  private static final String GET_CLIPBOARD = "getClipboard";
  private static final String SET_CLIPBOARD = "setClipboard";

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
      return getMobileClipboardText((HasClipboard) webDriver);
    }
    else {
      return defaultClipboard.getText();
    }
  }

  @Override
  public void setText(String text) {
    WebDriver webDriver = driver.getWebDriver();
    if (isMobile(webDriver)) {
      setMobileClipboardText((HasClipboard) webDriver, text);
    }
    else {
      defaultClipboard.setText(text);
    }
  }

  private String getMobileClipboardText(HasClipboard mobileDriver) {
    try {
      return mobileDriver.getClipboardText();
    }
    catch (WebDriverException e) {
      if (!isUnknownMethod(e)) throw e;
      registerLegacyCommand(mobileDriver, GET_CLIPBOARD, "/session/:sessionId/appium/device/get_clipboard");
      String base64Content = execute(mobileDriver, Map.entry(GET_CLIPBOARD, Map.of("contentType", PLAINTEXT)));
      return new String(Base64.getMimeDecoder().decode(requireNonNullElse(base64Content, "")), UTF_8);
    }
  }

  private void setMobileClipboardText(HasClipboard mobileDriver, String text) {
    try {
      mobileDriver.setClipboardText(text);
    }
    catch (WebDriverException e) {
      if (!isUnknownMethod(e)) throw e;
      registerLegacyCommand(mobileDriver, SET_CLIPBOARD, "/session/:sessionId/appium/device/set_clipboard");
      String base64Content = Base64.getMimeEncoder().encodeToString(text.getBytes(UTF_8));
      execute(mobileDriver, Map.entry(SET_CLIPBOARD, Map.of("content", base64Content, "contentType", PLAINTEXT)));
    }
  }

  /**
   * Appium java-client 11 removed legacy clipboard endpoints, so we need to register them manually.
   */
  private static void registerLegacyCommand(HasClipboard mobileDriver, String commandName, String url) {
    if (mobileDriver instanceof AppiumDriver appiumDriver) {
      appiumDriver.addCommand(POST, url, commandName);
    }
  }

  /**
   * Old Appium servers (e.g. XCUITest driver 7.x on BrowserStack) don't support "mobile: setClipboard" extension.
   * Appium java-client 11 doesn't fall back to the legacy clipboard endpoint anymore, so we do it ourselves.
   */
  private static boolean isUnknownMethod(WebDriverException e) {
    String message = e.getMessage();
    return message != null && message.contains("unknown method");
  }
}
