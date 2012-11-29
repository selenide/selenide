package com.codeborne.selenide.integrationtests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimeoutTest {
  @Test
  public void lookingForInexistingElementShouldFailFast() {
    navigateToAbsoluteUrl("http://jquery.com/"); // TODO Use something like localhost:8080/with-jquery
    
    long start = System.nanoTime();
    try {
      getWebDriver().findElement(By.id("inexistingElement"));
      fail("Looking for inexisting element should fail");
    }
    catch (NoSuchElementException expectedException) {
      long end = System.nanoTime();
      assertTrue("Looking for inexisting element took more than 1 ms: " + (end-start)/1000000 + " ms.", end-start < 1000000000);
    }
  }
}
