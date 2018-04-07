package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSelectedTextCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource selectElement = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetSelectedText getSelectedTextCommand;
  private GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);

  @Before
  public void setup() {
    getSelectedTextCommand = new GetSelectedText(getSelectedOptionCommand);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetSelectedText getSelectedText = new GetSelectedText();
    Field getSelectedOptionField = getSelectedText.getClass().getDeclaredField("getSelectedOption");
    getSelectedOptionField.setAccessible(true);
    GetSelectedOption getSelectedOption = (GetSelectedOption) getSelectedOptionField.get(getSelectedText);
    assertNotNull(getSelectedOption);
  }

  @Test
  public void testExecuteMethodWhenSelectedOptionReturnsNothing() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(null);
    assertNull(getSelectedTextCommand.execute(proxy, selectElement, new Object[] {"something more"}));
  }

  @Test
  public void testExecuteMethodWhenSelectedOptionReturnsElement() {
    when(getSelectedOptionCommand.execute(proxy, selectElement, Command.NO_ARGS)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getText()).thenReturn(elementText);
    assertEquals(elementText, getSelectedTextCommand.execute(proxy, selectElement, new Object[] {"something more"}));
  }
}
