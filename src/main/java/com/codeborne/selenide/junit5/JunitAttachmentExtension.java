package com.codeborne.selenide.junit5;

import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Extension for JUnit Attachments Jenkins Plugin.
 * <a href="https://plugins.jenkins.io/junit-attachments">JUnit Attachments Jenkins Plugin</a>
 * <br>
 * Using:
 * 1. @ExtendWith({JunitAttachmentExtension.class})
 * <br>
 * 2. After test fails, output [[ATTACHMENT|/absolute/path/to/screenshot]] automatically.
 * <br>
 */
public class JunitAttachmentExtension implements TestWatcher {
  @Override
  public void testFailed(ExtensionContext context, Throwable e) {
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
