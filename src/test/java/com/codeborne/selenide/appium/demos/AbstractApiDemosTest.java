package com.codeborne.selenide.appium.demos;

import com.codeborne.selenide.Configuration;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public abstract class AbstractApiDemosTest {
  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browserSize = null;
    Configuration.browser = AndroidDriverWithDemos.class.getName();
    open();
  }

  @SuppressWarnings("unchecked")
  protected AndroidDriver driver() {
    return (AndroidDriver) getWebDriver();
  }
}
