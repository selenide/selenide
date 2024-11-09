package com.codeborne.selenide.ex.custom;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.SelenideErrorFormatter;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.ex.Strings.join;

public class ErrorFormatterWithPageUrl extends SelenideErrorFormatter {
  @Override
  public String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs) {
    return join(super.generateErrorDetails(error, driver, screenshot, timeoutMs), pageUrl(driver));
  }

  protected String pageUrl(Driver driver) {
    try {
      return String.format("Page url: %s", driver.url());
    }
    catch (WebDriverException commandNotImplemented) {
      return "";
    }
  }

}
