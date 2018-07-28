package com.codeborne.selenide.commands;


import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetMaxLengthCommandTest implements WithAssertions {
  private SelenideElement mockedElement;
  private SelenideElement proxy;
  private WebElementSource locator;
  private GetMaxLength getMaxLengthCommand;

  @BeforeEach
  void setup() {
    getMaxLengthCommand = new GetMaxLength();
    proxy = mock(SelenideElement.class);
    mockedElement = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethod() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("maxlength")).thenReturn(elementAttribute);
    assertThat(getMaxLengthCommand.execute(proxy, locator, new Object[] {argument, "something else"}))
      .isEqualTo(elementAttribute);
  }

}
