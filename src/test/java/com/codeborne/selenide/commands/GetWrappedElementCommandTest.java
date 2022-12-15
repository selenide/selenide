package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetWrappedElementCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();
  private final GetWrappedElement command = new GetWrappedElement();

  @Test
  void getsUnderlyingWebElement() {
    when(locator.getWebElement()).thenReturn(mockedElement);

    assertThat(command.execute(proxy, locator, null))
      .isEqualTo(mockedElement);
  }
}
