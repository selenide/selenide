package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;

public class SelenideWait extends FluentWait<WebDriver> {
  public SelenideWait(WebDriver input) {
    super(input);
    withTimeout(Duration.of(timeout, ChronoUnit.MILLIS));
    pollingEvery(Duration.of(pollingInterval, ChronoUnit.MILLIS));
  }
}
