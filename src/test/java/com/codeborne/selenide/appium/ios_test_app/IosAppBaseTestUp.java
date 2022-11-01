package com.codeborne.selenide.appium.ios_test_app;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class IosAppBaseTestUp {

  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browser = IosTestAppDriverFactory.class.getName();
    Configuration.browserSize = null;
    open();
  }
}
