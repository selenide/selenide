package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDataAttributeCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetDataAttribute getDataAttributeCommand;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    getDataAttributeCommand = new GetDataAttribute();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWithDataAttribute() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("data-" + argument)).thenReturn(elementAttribute);
    assertThat(getDataAttributeCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(elementAttribute);
  }

  @Test
  void testExecuteMethodWithNoDataAttribute() {
    String argument = "class";
    when(mockedElement.getAttribute("data-" + argument)).thenReturn(null);
    assertThat(getDataAttributeCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isNull();
  }
}
