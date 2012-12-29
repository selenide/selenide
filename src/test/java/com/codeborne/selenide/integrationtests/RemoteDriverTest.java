package com.codeborne.selenide.integrationtests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static java.lang.Thread.currentThread;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * How to set up Selenium Grid:
 * http://code.google.com/p/selenium/wiki/Grid2
 */
public class RemoteDriverTest {

  @BeforeClass
  public static void init() {
    System.setProperty("remote", "http://localhost:4444/wd/hub");
  }

  @AfterClass
  public static void clear() {
    closeWebDriver();
    System.clearProperty("remote");
  }

  @Test
  public void testRemoteDriver() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
    assertThat($("body > h1").getText(), equalTo("Page without JQuery")); // long style
    $("body > h1").shouldHave(text("Page without JQuery")); // short style
  }
}
