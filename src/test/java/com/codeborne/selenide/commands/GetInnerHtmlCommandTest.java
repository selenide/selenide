package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetInnerHtmlCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetInnerHtml getInnerHtmlCommand = new GetInnerHtml();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void uses_innerHTML_attribute() {
    when(locator.driver()).thenReturn(new DriverStub("firefox"));

    when(mockedElement.getAttribute("innerHTML")).thenReturn("hello");
    assertThat(getInnerHtmlCommand.execute(proxy, locator, null)).isEqualTo("hello");
  }
}
