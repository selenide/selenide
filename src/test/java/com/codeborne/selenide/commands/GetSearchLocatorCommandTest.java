package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSearchLocatorCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final GetSearchLocator getSearchLocatorCommand = new GetSearchLocator();

  @Test
  void returnsSearchCriteria() {
    when(locator.getSearchLocator()).thenReturn("//div[id='sample']");
    assertThat(getSearchLocatorCommand.execute(proxy, locator, null))
      .isEqualTo("//div[id='sample']");
  }
}
