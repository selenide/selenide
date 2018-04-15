package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTextCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetText getTextCommand;
  private GetSelectedText getSelectedTextCommand = mock(GetSelectedText.class);

  @Before
  public void setup() {
    getTextCommand = new GetText(getSelectedTextCommand);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testDefaultConstructor() throws IllegalAccessException, NoSuchFieldException {
    GetText getText = new GetText();
    Field getSelectedTextField = getText.getClass().getDeclaredField("getSelectedText");
    getSelectedTextField.setAccessible(true);
    GetSelectedText getSelectedText = (GetSelectedText) getSelectedTextField.get(getText);
    assertNotNull(getSelectedText);
  }

  @Test
  public void testExecuteMethodWithSelectElement() {
    when(mockedElement.getTagName()).thenReturn("select");
    Object[] args = {"something more"};
    String selectedElementText = "Selected Text";
    when(getSelectedTextCommand.execute(proxy, locator, args)).thenReturn(selectedElementText);
    assertEquals(selectedElementText, getTextCommand.execute(proxy, locator, args));
  }

  @Test
  public void testExecuteMethodWithNotSelectElement() {
    when(mockedElement.getTagName()).thenReturn("href");
    String text = "This is text";
    when(mockedElement.getText()).thenReturn(text);
    assertEquals(text, getTextCommand.execute(proxy, locator, new Object[]{"something more"}));
  }
}
