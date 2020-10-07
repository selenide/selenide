package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSearchCriteriaCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final GetSearchCriteria getSearchCriteriaCommand = new GetSearchCriteria();
  private final String defaultSearchCriteria = "by.xpath";

  @BeforeEach
  void setup() {
    when(locator.getSearchCriteria()).thenReturn(defaultSearchCriteria);
  }

  @Test
  void testExecuteMethod() {
    assertThat(getSearchCriteriaCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isEqualTo(defaultSearchCriteria);
  }
}
