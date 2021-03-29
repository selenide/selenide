package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSiblingByIndexTest {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);

  private final Sibling sibling = new GetSiblingByIndex();

  @Test
  void shouldCanExecuteWhenParametersCountIsOneAndInteger() {
    assertThat(sibling.canExecute(new Object[]{0})).isTrue();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsOneAndString() {
    assertThat(sibling.canExecute(new Object[]{"a"})).isFalse();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsTwo() {
    assertThat(sibling.canExecute(new Object[]{0, 1})).isFalse();
  }

  @Test
  void shouldReturnSibling() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);
    SelenideElement actual = sibling.execute(proxy, locator, new Object[]{0});

    assertThat(actual).isEqualTo(mockedElement);
  }
}
