package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAliasCommandTest implements WithAssertions {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final GetAlias getAlias = new GetAlias();

  @Test
  void testExecuteMethod() {
    String alias = "my element";
    when(locator.getAlias()).thenReturn(alias);
    assertThat(getAlias.execute(proxy, locator, new Object[]{}))
      .as("should return element alias")
      .isEqualTo(alias);
  }

  @Test
  void testReturnNullWhenAliasNotSet() {
    assertThat(getAlias.execute(proxy, locator, new Object[]{}))
      .as("should return null when alias is not set")
      .isNull();
  }

}
