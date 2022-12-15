package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetCssValueCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final WebElement mockedElement = mock();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void execute() {
    when(mockedElement.getCssValue(any())).thenReturn("none");
    assertThat(new GetCssValue().execute(proxy, locator, new Object[]{"display", ""}))
      .isEqualTo("none");
    verify(mockedElement).getCssValue("display");
  }
}
