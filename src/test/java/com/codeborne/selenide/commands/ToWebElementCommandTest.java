package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ToWebElementCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final ToWebElement command = new ToWebElement();
  private final WebElement webElement = mock();

  @Test
  void returnsUnderlyingWebElement() {
    when(locator.getWebElement()).thenReturn(webElement);
    assertThat(command.execute(proxy, locator, new Object[]{})).isEqualTo(webElement);
    verify(locator).getWebElement();
  }
}
