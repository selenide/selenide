package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.impl.Alias.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAliasCommandTest {

  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final GetAlias getAlias = new GetAlias();

  @Test
  void returns_element_alias_if_defined() {
    when(locator.getAlias()).thenReturn(new Alias("my element"));
    assertThat(getAlias.execute(proxy, locator, new Object[]{}))
      .as("should return element alias")
      .isEqualTo("my element");
  }

  @Test
  void returns_null_ifAliasNotSet() {
    when(locator.getAlias()).thenReturn(NONE);
    assertThat(getAlias.execute(proxy, locator, new Object[]{}))
      .as("should return null when alias is not set")
      .isNull();
  }
}
