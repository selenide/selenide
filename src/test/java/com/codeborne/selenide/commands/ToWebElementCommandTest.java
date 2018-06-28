package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToWebElementCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ToWebElement toWebElementCommand;
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    toWebElementCommand = new ToWebElement();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testExecuteMethod() {
    assertThat(toWebElementCommand.execute(proxy, locator, new Object[]{}))
      .isEqualTo(mockedFoundElement);
  }
}
