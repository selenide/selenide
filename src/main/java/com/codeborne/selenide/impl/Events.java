package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Arrays.asList;

@ParametersAreNonnullByDefault
public class Events {
  public static Events events = new Events(LoggerFactory.getLogger(Events.class));

  private final JavaScript js = new JavaScript("trigger-events.js");
  private final Logger log;

  Events(Logger log) {
    this.log = log;
  }

  public void fireEvent(Driver driver, WebElement element, String... event) {
    try {
      executeJavaScript(driver, element, event);
    }
    catch (StaleElementReferenceException ignore) {
    }
    catch (Exception e) {
      log.warn("Failed to trigger events {}: {}", asList(event), Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  void executeJavaScript(Driver driver, WebElement element, String... event) {
    js.execute(driver, element, event);
  }
}
