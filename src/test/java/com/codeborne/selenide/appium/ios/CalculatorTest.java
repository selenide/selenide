package com.codeborne.selenide.appium.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

/**
 * Inspired by <a href="https://applitools.com/blog/how-to-write-appium-ios-test/">...</a>
 * but migrated to Selenide.
 */
@ExtendWith(TextReportExtension.class)
public class CalculatorTest {
  @Test
  void addNumbers() {
    closeWebDriver();
    Configuration.browser = IosTestAppDriverFactory.class.getName();
    Configuration.browserSize = null;
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
    open();

    CalculatorPage page = new CalculatorPage();

    page
      .enterTwoNumbersAndCompute("4", "6")
      .verifySum("10");

    page
      .enterTwoNumbersAndCompute("111111", "222222")
      .verifySum("333333");
  }
}

