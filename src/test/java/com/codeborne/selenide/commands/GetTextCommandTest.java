package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetTextCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetSelectedOptionText getSelectedOptionTextCommand = mock(GetSelectedOptionText.class);
  private final GetText command = new GetText(getSelectedOptionTextCommand);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void selectElement() {
    when(mockedElement.getTagName()).thenReturn("select");
    when(getSelectedOptionTextCommand.execute(any(), any(), any())).thenReturn("Selected Text");

    assertThat(command.execute(proxy, locator, null)).isEqualTo("Selected Text");

    verify(getSelectedOptionTextCommand).execute(proxy, locator, null);
  }

  @Test
  void nonSelectElement() {
    when(mockedElement.getTagName()).thenReturn("a");
    when(mockedElement.getText()).thenReturn("This is text");
    assertThat(command.execute(proxy, locator, null)).isEqualTo("This is text");
  }
}
