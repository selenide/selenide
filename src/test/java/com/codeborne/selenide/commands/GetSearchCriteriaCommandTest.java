package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSearchCriteriaCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final GetSearchCriteria getSearchCriteriaCommand = new GetSearchCriteria();

  @Test
  void returnsSearchCriteria() {
    when(locator.getSearchCriteria()).thenReturn("by.xpath");
    assertThat(getSearchCriteriaCommand.execute(proxy, locator, null))
      .isEqualTo("by.xpath");
  }
}
