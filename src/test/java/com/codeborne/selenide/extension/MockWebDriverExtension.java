package com.codeborne.selenide.extension;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.mockito.Mockito.mock;

/**
 * @author Aliaksandr Rasolka
 * @since 1.0.0
 */
public class MockWebDriverExtension implements BeforeAllCallback, AfterAllCallback {
  @Override
  public void beforeAll(final ExtensionContext context) {
    webdriverContainer = mock(WebDriverThreadLocalContainer.class);
  }

  @Override
  public void afterAll(final ExtensionContext context) {
    webdriverContainer = new WebDriverThreadLocalContainer();
  }
}
