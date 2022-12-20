package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetDataAttributeCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement webElement = mock();
  private final GetDataAttribute command = new GetDataAttribute();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(webElement);
  }

  @Test
  void returnsValueOfDataAttribute() {
    when(webElement.getAttribute(any())).thenReturn("hello");
    assertThat(command.execute(proxy, locator, new Object[]{"test-id"}))
      .isEqualTo("hello");
    verify(webElement).getAttribute("data-test-id");
  }

  @Test
  void returnsNullIfDataAttributeIsMissing() {
    when(webElement.getAttribute(any())).thenReturn(null);
    assertThat(command.execute(proxy, locator, new Object[]{"test-id"}))
      .isNull();
    verify(webElement).getAttribute("data-test-id");
  }
}
