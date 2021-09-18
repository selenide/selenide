package com.codeborne.selenide.junit;

import org.junit.rules.ExternalResource;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * Usage:
 * <pre>  {@literal @}Rule
 * public BrowserStrategy perTest = new BrowserStrategy();</pre>
 * or
 * <pre>  {@literal @}ClassRule
 * public static BrowserStrategy perClass = new BrowserStrategy();</pre>
 */
public class BrowserStrategy extends ExternalResource {
  public BrowserStrategy() {
  }

  @Override
  protected void after() {
    closeWebDriver();
  }
}
