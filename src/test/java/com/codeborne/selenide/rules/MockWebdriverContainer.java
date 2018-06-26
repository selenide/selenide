package com.codeborne.selenide.rules;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.junit.rules.ExternalResource;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.mockito.Mockito.mock;

class MockWebdriverContainer extends ExternalResource {
  @Override
  protected void before() {
    webdriverContainer = mock(WebDriverThreadLocalContainer.class);
  }

  @Override
  protected void after() {
    webdriverContainer = new WebDriverThreadLocalContainer();
  }
}
