package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ToWebElementCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final ToWebElement toWebElementCommand = new ToWebElement();
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testExecuteMethod() {
    assertThat(toWebElementCommand.execute(proxy, locator, new Object[]{}))
      .isEqualTo(mockedFoundElement);
  }
}
