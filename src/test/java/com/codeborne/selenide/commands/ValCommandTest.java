package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Command.NO_ARGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ValCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final GetValue getValue = mock();
  private final SetValue setValue = mock();
  private final Val command = new Val(getValue, setValue);

  @Test
  void returnsValue_whenCalledWithoutArguments() {
    when(getValue.execute(any(), any(), any())).thenReturn("some value");
    assertThat(command.execute(proxy, locator, null)).isEqualTo("some value");
    verify(getValue).execute(proxy, locator, NO_ARGS);
  }

  @Test
  void returnsValue_whenCalledWithEmptyArguments() {
    when(getValue.execute(any(), any(), any())).thenReturn("some value");
    assertThat(command.execute(proxy, locator, new Object[]{})).isEqualTo("some value");
    verify(getValue).execute(proxy, locator, NO_ARGS);
  }

  @Test
  void setsValue_whenCalledWithStringArgument() {
    assertThat(command.execute(proxy, locator, new Object[]{"new value"})).isEqualTo(proxy);
    verify(setValue).execute(proxy, locator, new Object[]{"new value"});
  }
}
