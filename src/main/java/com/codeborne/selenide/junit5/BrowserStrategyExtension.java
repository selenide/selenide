package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
public class BrowserStrategyExtension implements AfterAllCallback {
  @Override
  public void afterAll(final ExtensionContext context) {
    closeWebDriver();
  }
}
