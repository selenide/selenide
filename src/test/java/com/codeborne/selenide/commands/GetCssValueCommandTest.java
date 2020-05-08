package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetCssValueCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private WebElement mockedElement;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethod() {
    GetCssValue getCssValue = new GetCssValue();
    String argument = "display";
    String elementAttribute = "none";
    when(mockedElement.getCssValue(argument)).thenReturn(elementAttribute);
    assertThat(getCssValue.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(elementAttribute);
  }
}
