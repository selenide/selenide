package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSiblingByTagAndIndexTest {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);

  private final Sibling sibling = new GetSiblingByTagAndIndex();

  @Test
  void shouldCanExecuteWhenParametersCountIsTwoStringAndInteger() {
    assertThat(sibling.canExecute(new Object[]{"a", 0})).isTrue();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsTwoIntegerAndInteger() {
    assertThat(sibling.canExecute(new Object[]{0, 1})).isFalse();
  }

  @Test
  void shouldNotCanExecuteWhenParametersCountIsOne() {
    assertThat(sibling.canExecute(new Object[]{0})).isFalse();
  }

  @Test
  void shouldReturnSibling() {
    when(locator.find(any(), any(), anyInt())).thenReturn(mockedElement);
    SelenideElement actual = sibling.execute(proxy, locator, new Object[]{"a", 0});

    assertThat(actual).isEqualTo(mockedElement);
  }
}
