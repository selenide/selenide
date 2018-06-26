package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.UnitTest;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetInnerTextCommandTest extends UnitTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetInnerText getInnerTextCommand;

  @BeforeEach
  void setup() {
    getInnerTextCommand = new GetInnerText();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWhenNotIE() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("textContent")).thenReturn(elementAttribute);
    assertThat(getInnerTextCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(elementAttribute);
  }
}
