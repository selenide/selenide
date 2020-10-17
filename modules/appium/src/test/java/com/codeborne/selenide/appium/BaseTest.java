package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

public class BaseTest {
  @BeforeEach
  public void setUp() {
    Configuration.startMaximized = false;
    Configuration.browserSize = null;
    Configuration.browser = AndroidDriverProvider.class.getName();
    open();
  }

  @AfterEach
  public void tearDown() {
    closeWebDriver();
  }
}
