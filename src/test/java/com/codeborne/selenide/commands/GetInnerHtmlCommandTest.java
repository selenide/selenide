package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetInnerHtmlCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement webElement = mock();
  private final GetInnerHtml command = new GetInnerHtml();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(webElement);
  }

  @Test
  void uses_innerHTML_attribute() {
    when(locator.driver()).thenReturn(new DriverStub("firefox"));
    when(webElement.getAttribute(any())).thenReturn("hello");

    assertThat(command.execute(proxy, locator, null)).isEqualTo("hello");

    verify(webElement).getAttribute("innerHTML");
  }
}
