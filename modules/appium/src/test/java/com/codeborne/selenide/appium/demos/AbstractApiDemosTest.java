package com.codeborne.selenide.appium.demos;

import com.codeborne.selenide.Configuration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public abstract class AbstractApiDemosTest {
  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.startMaximized = false;
    Configuration.browserSize = null;
    Configuration.browser = AndroidDriverWithDemos.class.getName();
    open();
  }

  @SuppressWarnings("unchecked")
  protected AndroidDriver<AndroidElement> driver() {
    return (AndroidDriver<AndroidElement>) getWebDriver();
  }
}
