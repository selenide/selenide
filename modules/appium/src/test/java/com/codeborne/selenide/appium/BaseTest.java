package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

public class BaseTest {
  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browserSize = null;
    Configuration.browser = AndroidDriverProvider.class.getName();
//    TODO support these listeners as well
//    WebDriverRunner.addListener(new WebDriverListener() {
//      @Override
//      public void beforeAnyCall(Object target, Method method, Object[] args) {
//      }
//    });
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
    open();
  }

  @AfterEach
  public void tearDown() {
    closeWebDriver();
  }
}
