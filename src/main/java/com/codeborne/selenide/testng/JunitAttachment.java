package com.codeborne.selenide.testng;

import com.codeborne.selenide.ex.UIAssertionError;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Listener for JUnit Attachments Jenkins Plugin.
 * <a href="https://plugins.jenkins.io/junit-attachments">JUnit Attachments Jenkins Plugin</a>
 * <br>
 * Using:
 * 1. Annotate your test class with {@code @Listeners({ JunitAttachment.class})}
 * <br>
 * 2. After test fails, output [[ATTACHMENT|/absolute/path/to/screenshot]] automatically.
 * <br>
 */
public class JunitAttachment implements IInvokedMethodListener {
  @Override
  public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
  }

  @Override
  public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult testResult) {
    if (!testResult.isSuccess()) {
      Throwable e = testResult.getThrowable();
      if (e instanceof UIAssertionError) {
        UIAssertionError uiAssertionError = (com.codeborne.selenide.ex.UIAssertionError) e;
        String screenshot = uiAssertionError.getScreenshot();
        if (screenshot.equals("")) {
          return;
        }
        try {
          URL screenshotURL = new URL(screenshot);
          System.out.println("[[ATTACHMENT|" + screenshotURL.getFile() + "]]");
        } catch (MalformedURLException ex) {
          System.out.println("JunitAttachment Rule: screenshot " + screenshot + " is not a valid URL");
        }
      }
    }
  }
}
