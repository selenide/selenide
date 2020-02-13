package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPrecedingCommandTest implements WithAssertions {

  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetPreceding getPrecedingCommand;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    getPrecedingCommand = new GetPreceding();
  }

  @Test
  void testExecuteMethod() {
    when(locator.find(proxy,
      By.xpath("preceding-sibling::*[1]"),
      0)).
      thenReturn(mockedElement);
    assertThat(getPrecedingCommand.execute(proxy, locator, new Object[]{0}))
      .isEqualTo(mockedElement);
  }
}
