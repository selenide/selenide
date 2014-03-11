package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import static org.junit.Assert.*;

public class CleanupTest {
  @Test
  public void cleansWebDriverExceptionMessage() {
    String webdriverException = "org.openqa.selenium.NoSuchElementException: The element could not be found (WARNING: The server did not provide any stacktrace information)\n" +
        "Command duration or timeout: 21 milliseconds\n" +
        "For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html\n" +
        "Build info: version: '2.29.1', revision: 'dfb1306b85be4934d23c123122e06e602a15e446', time: '2013-01-22 12:58:05'\n" +
        "System info: os.name: 'Linux', os.arch: 'amd64', os.version: '3.5.0-23-generic', java.version: '1.7.0_10'\n" +
        "Session ID: 610138404f5c180a4f3153785e66c528\n" +
        "Driver info: org.openqa.selenium.chrome.ChromeDriver\n" +
        "Capabilities [{platform=LINUX, chrome.chromedriverVersion=26.0.1383.0, acceptSslCerts=false, javascriptEnabled=true, browserName=chrome, rotatable=false, locationContextEnabled=false, version=24.0.1312.56, cssSelectorsEnabled=true, databaseEnabled=false, handlesAlerts=true, browserConnectionEnabled=false, webStorageEnabled=true, nativeEvents=true, applicationCacheEnabled=false, takesScreenshot=true}]";
    String expectedException = "NoSuchElementException: The element could not be found";
    assertEquals(expectedException, Cleanup.of.webdriverExceptionMessage(webdriverException));
  }

  @Test
  public void detectsIfWebdriverReportedInvalidSelectorError() {
    assertFalse(Cleanup.of.isInvalidSelectorError(new WebDriverException("Ups!")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new InvalidSelectorException("Wrong xpath")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new WebDriverException("An invalid or illegal string was specified\n")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new WebDriverException("invalid element state: Failed to execute query: '//input[:attr='al]' is not a valid selector.\n")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new WebDriverException("Invalid selectors: //input[:attr='al]")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new WebDriverException("{\"errorMessage\":\"SYNTAX_ERR: DOM Exception 12\",,\"post\":\"{\\\"using\\\":\\\"css selector\\\",\\\"value\\\":\\\"//input[:attr='al]\\\"}\"}}\n")));
    assertTrue(Cleanup.of.isInvalidSelectorError(new WebDriverException("{\"errorMessage\":\"Unable to locate an element with the xpath expression //xxx[@' because of the following error:\\nError: INVALID_EXPRESSION_ERR: DOM XPath Exception 51\"}}\n")));

    RuntimeException cssException = new RuntimeException("Invalid selectors: //input[:attr='al]");
    NoSuchElementException error = new NoSuchElementException("Unable to locate element using css", cssException);
    assertTrue(Cleanup.of.isInvalidSelectorError(error));
  }
}
