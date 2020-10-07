package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetDataAttributeCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetDataAttribute getDataAttributeCommand = new GetDataAttribute();

  @BeforeEach
  void setup() {
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
