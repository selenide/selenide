package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * By using this extension browser will be automatically closed after all tests in the current container.
 * <br>
 * To use this extension, extend your test class with it:
 * <br>
 * {@code @ExtendWith({BrowserStrategyExtension.class}}
 * <br>
 * Or register extension in test class:
 * <br>
 * {@code @RegisterExtension static BrowserStrategyExtension browserStrategy = new BrowserStrategyExtension();}
 * <br>
 *
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
@ParametersAreNonnullByDefault
public class BrowserStrategyExtension implements AfterAllCallback {
  @Override
  public void afterAll(final ExtensionContext context) {
    closeWebDriver();
  }
}
