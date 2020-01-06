package com.codeborne.selenide.junit;

import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Rule for JUnit Attachments Jenkins Plugin.
 * <a href="https://plugins.jenkins.io/junit-attachments">JUnit Attachments Jenkins Plugin</a>
 * <br>
 * Using:
 * 1. Add rule for test class: {@code @Rule public JunitAttachment junitAttachment = new JunitAttachment();}
 * <br>
 * 2. After test fails, output [[ATTACHMENT|/absolute/path/to/screenshot]] automatically.
 * <br>
 */
public class JunitAttachment extends TestWatcher {
  @Override
  protected void failed(Throwable e, Description description) {
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
