package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetInnerHtmlCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetInnerHtml getInnerHtmlCommand;

  @BeforeEach
  void setup() {
    getInnerHtmlCommand = new GetInnerHtml();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWhenNotHtmlUnit() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("innerHTML")).thenReturn(elementAttribute);
    assertThat(getInnerHtmlCommand.execute(proxy, locator, new Object[]{argument, "something more"}))
      .isEqualTo(elementAttribute);
  }
}
