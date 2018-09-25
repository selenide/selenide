package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetInnerTextCommandTest implements WithAssertions {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetInnerText getInnerTextCommand = new GetInnerText();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void uses_innerText_attribute_if_IE() {
    when(locator.driver()).thenReturn(new DriverStub("ie"));

    when(mockedElement.getAttribute("innerText")).thenReturn("hello");
    assertThat(getInnerTextCommand.execute(proxy, locator, null)).isEqualTo("hello");
  }

  @Test
  void uses_textContent_attribute_if_not_IE() {
    when(locator.driver()).thenReturn(new DriverStub("firefox"));

    when(mockedElement.getAttribute("textContent")).thenReturn("hello");
    assertThat(getInnerTextCommand.execute(proxy, locator, null)).isEqualTo("hello");
  }
}
