package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ParametersAreNonnullByDefault
public class SelenideWait extends FluentWait<WebDriver> {
  public SelenideWait(WebDriver input, long timeout, long pollingInterval) {
    super(input);
    withTimeout(Duration.of(timeout, ChronoUnit.MILLIS));
    pollingEvery(Duration.of(pollingInterval, ChronoUnit.MILLIS));
  }
}
