package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimeoutTest {
  @Before
  public void openTestPageWithJQuery() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void lookingForNonExistingElementShouldFailFast() {
    long start = System.nanoTime();
    try {
      getWebDriver().findElement(By.id("nonExistingElement"));
      fail("Looking for non-existing element should fail");
    } catch (NoSuchElementException expectedException) {
      long end = System.nanoTime();
      assertTrue("Looking for non-existing element took more than 1 ms: " + (end - start) / 1000000 + " ms.", end - start < 1000000000L);
    }
  }
}
