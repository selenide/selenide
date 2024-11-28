package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.closeWebDriver;

/**
 * JUnit extension to automatically close browser after all tests.
 * <p>
 * To use this extension, annotate your test class:
 * <pre>
 * {@code @ExtendWith({BrowserStrategyExtension.class}}
 * </pre>
 *
 * Or register the extension in test class:
 * <pre>
 * {@code @RegisterExtension static BrowserStrategyExtension browserStrategy = new BrowserStrategyExtension();}
 * </pre>
 *
 * @author Aliaksandr Rasolka
 */
public class BrowserStrategyExtension implements AfterAllCallback {
  @Override
  public void afterAll(final ExtensionContext context) {
    closeWebDriver();
  }
}
