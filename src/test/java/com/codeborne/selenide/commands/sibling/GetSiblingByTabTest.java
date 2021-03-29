package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSiblingByTabTest {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);

  private final Sibling sibling = new GetSiblingByTab();

  @Test
  void shouldCanExecuteWhenParametersCountIsOneAndString() {
    assertThat(sibling.canExecute(new Object[]{"a"})).isTrue();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsOneAndInteger() {
    assertThat(sibling.canExecute(new Object[]{0})).isFalse();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsTwo() {
    assertThat(sibling.canExecute(new Object[]{"a", "b"})).isFalse();
  }

  @Test
  void shouldReturnSibling() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);
    SelenideElement actual = sibling.execute(proxy, locator, new Object[]{"a"});

    assertThat(actual).isEqualTo(mockedElement);
  }
}
