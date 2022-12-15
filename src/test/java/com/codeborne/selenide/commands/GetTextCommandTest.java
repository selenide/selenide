package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.selectWithSelectedText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetTextCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final SelenideElement mockedElement = mock();
  private final GetText command = new GetText(selectWithSelectedText("Selected Text"));

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void selectElement() {
    when(mockedElement.getTagName()).thenReturn("select");

    assertThat(command.execute(proxy, locator, null)).isEqualTo("Selected Text");
  }

  @Test
  void nonSelectElement() {
    when(mockedElement.getTagName()).thenReturn("a");
    when(mockedElement.getText()).thenReturn("This is text");
    assertThat(command.execute(proxy, locator, null)).isEqualTo("This is text");
  }
}
