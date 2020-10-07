package com.codeborne.selenide.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

final class CleanupTest implements WithAssertions {
  @Test
  void cleansWebDriverExceptionMessage() {
    String webDriverException = "org.openqa.selenium.NoSuchElementException: " +
      "The element could not be found (WARNING: The server did not provide any stacktrace information)\n" +
      "Command duration or timeout: 21 milliseconds\n" +
      "For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html\n" +
      "Build info: version: '2.29.1', revision: 'dfb1306b85be4934d23c123122e06e', time: '2013-01-22 12:58:05'\n" +
      "System info: os.name: 'Linux', os.arch: 'amd64', os.version: '3.5.0-23-generic', java.version: '1.7.0_10'\n" +
      "Session ID: 610138404f5c180a4f3153785e66c528\n" +
      "Driver info: org.openqa.selenium.chrome.ChromeDriver\n" +
      "Capabilities [{platform=LINUX, chrome.chromedriverVersion=26.0.1383.0, acceptSslCerts=false, " +
      "javascriptEnabled=true, browserName=chrome, rotatable=false, locationContextEnabled=false, " +
      "version=24.0.1312.56, cssSelectorsEnabled=true, databaseEnabled=false, handlesAlerts=true, " +
      "browserConnectionEnabled=false, webStorageEnabled=true, nativeEvents=true, applicationCacheEnabled=false, " +
      "takesScreenshot=true}]";
    String expectedException = "NoSuchElementException: The element could not be found";
    assertThat(Cleanup.of.webdriverExceptionMessage(webDriverException))
      .isEqualTo(expectedException);
  }

  @Test
  void detectsIfWebdriverReportedInvalidSelectorError() {
    assertThat(Cleanup.of.isInvalidSelectorError(null))
      .isFalse();
    assertThat(Cleanup.of.isInvalidSelectorError(new NullPointerException()))
      .isFalse();
    assertThat(Cleanup.of.isInvalidSelectorError(new IllegalArgumentException()))
      .isFalse();
    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("Ups!")))
      .isFalse();
    assertThat(Cleanup.of.isInvalidSelectorError(new InvalidSelectorException("Wrong xpath")))
      .isTrue();
    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("An invalid or illegal string was specified\n")))
      .isTrue();
    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("invalid element state: " +
      "Failed to execute query: '//input[:attr='al]' is not a valid selector.\n")))
      .isTrue();
    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("Invalid selectors: //input[:attr='al]")))
      .isTrue();
    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("{\"errorMessage\":" +
      "\"SYNTAX_ERR: DOM Exception 12\",,\"post\":\"{\\\"using\\\":\\\"css selector\\\"," +
      "\\\"value\\\":\\\"//input[:attr='al]\\\"}\"}}\n")))
      .isTrue();

    assertThat(Cleanup.of.isInvalidSelectorError(new WebDriverException("{\"errorMessage\":" +
      "\"Unable to locate an element with the xpath expression //xxx[@' because of the " +
      "following error:\\nError: INVALID_EXPRESSION_ERR: DOM XPath Exception 51\"}}\n")))
      .isTrue();

    RuntimeException cssException = new RuntimeException("Invalid selectors: //input[:attr='al]");
    NoSuchElementException error = new NoSuchElementException("Unable to locate element using css", cssException);
    assertThat(Cleanup.of.isInvalidSelectorError(error))
      .isTrue();
  }
}
