package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.mockElement;
import static org.mockito.Mockito.*;

final class GetSelectedTextCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectElement = mock(WebElementSource.class);
  private final GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);
  private final GetSelectedText command = new GetSelectedText(getSelectedOptionCommand);

  @Test
  void returnsTextOfSelectedOption() {
    SelenideElement option = mockElement("option", "Option text");
    when(getSelectedOptionCommand.execute(any(), any(), any())).thenReturn(option);

    assertThat(command.execute(proxy, selectElement, null)).isEqualTo("Option text");

    verify(getSelectedOptionCommand).execute(proxy, selectElement, Command.NO_ARGS);
  }
}
