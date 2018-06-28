package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSelectedTextCommandTest implements WithAssertions {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource selectElement = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetSelectedText getSelectedTextCommand;
  private GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);

  @BeforeEach
  void setup() {
    getSelectedTextCommand = new GetSelectedText(getSelectedOptionCommand);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetSelectedText getSelectedText = new GetSelectedText();
    Field getSelectedOptionField = getSelectedText.getClass().getDeclaredField("getSelectedOption");
    getSelectedOptionField.setAccessible(true);
    GetSelectedOption getSelectedOption = (GetSelectedOption) getSelectedOptionField.get(getSelectedText);
    assertThat(getSelectedOption)
      .isNotNull();
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsNothing() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(null);
    assertThat(getSelectedTextCommand.execute(proxy, selectElement, new Object[]{"something more"}))
      .isNullOrEmpty();
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsElement() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getText()).thenReturn(elementText);
    assertThat(getSelectedTextCommand.execute(proxy, selectElement, new Object[]{"something more"}))
      .isEqualTo(elementText);
  }
}
