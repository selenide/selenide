package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSelectedTextCommandTest {
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
    Assertions.assertNotNull(getSelectedOption);
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsNothing() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(null);
    Assertions.assertNull(getSelectedTextCommand.execute(proxy, selectElement, new Object[]{"something more"}));
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsElement() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getText()).thenReturn(elementText);
    Assertions.assertEquals(elementText, getSelectedTextCommand.execute(proxy, selectElement, new Object[]{"something more"}));
  }
}
