package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.closeWebDriver;

/**
 * By using this extension browser will be automatically closed after each test.
 * <br>
 * To use this extension, extend your test class with it:
 * <br>
 * {@code @ExtendWith({BrowserPerTestStrategyExtension.class}}
 * <br>
 * Or register extension in test class:
 * <br>
 * {@code @RegisterExtension static BrowserPerTestStrategyExtension browserPerTestStrategy = new BrowserPerTestStrategyExtension();}
 * <br>
 *
 * @author simple-elf
 */
public class BrowserPerTestStrategyExtension implements AfterEachCallback {
  @Override
  public void afterEach(final ExtensionContext context) {
    closeWebDriver();
  }
}
