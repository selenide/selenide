package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.sibling.Sibling;
import com.codeborne.selenide.impl.WebElementSource;
import org.apache.commons.lang3.NotImplementedException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

final class GetSiblingCommandTest implements WithAssertions {

  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final Sibling sibling = mock(Sibling.class);
  private final GetSibling getSiblingCommand = new GetSibling(singletonList(sibling));

  @Test
  void executeMethod() {
    Object[] args = {0};
    when(sibling.canExecute(args)).thenReturn(true);
    when(sibling.execute(proxy, locator, args)).thenReturn(mockedElement);

    assertThat(getSiblingCommand.execute(proxy, locator, args)).isEqualTo(mockedElement);

    verify(sibling).canExecute(args);
    verify(sibling).execute(proxy, locator, args);
  }

  @Test
  void shouldThrowExceptionWhenArgsNull() {
    assertThatThrownBy(() -> getSiblingCommand.execute(proxy, locator, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Missing arguments");
  }

  @Test
  void shouldThrowExceptionWhenMethodNotImplemented() {
    assertThatThrownBy(() -> getSiblingCommand.execute(proxy, locator, new Object[]{1, 2, 3, 4, 5}))
      .isInstanceOf(NotImplementedException.class)
      .hasMessage("Not implemented");
  }
}
