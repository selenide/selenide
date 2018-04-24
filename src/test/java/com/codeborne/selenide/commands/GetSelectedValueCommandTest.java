package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSelectedValueCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource selectElement = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetSelectedValue getSelectedValueCommand;
  private GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);

  @Before
  public void setup() {
    getSelectedValueCommand = new GetSelectedValue(getSelectedOptionCommand);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetSelectedValue getSelectedText = new GetSelectedValue();
    Field getSelectedOptionField = getSelectedText.getClass().getDeclaredField("getSelectedOption");
    getSelectedOptionField.setAccessible(true);
    GetSelectedOption getSelectedOption = (GetSelectedOption) getSelectedOptionField.get(getSelectedText);
    assertNotNull(getSelectedOption);
  }

  @Test
  public void testExecuteMethodWhenSelectedOptionReturnsNothing() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(null);
    assertNull(getSelectedValueCommand.execute(proxy, selectElement, args));
  }

  @Test
  public void testExecuteMethodWhenSelectedOptionReturnsElement() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getAttribute("value")).thenReturn(elementText);
    assertEquals(elementText, getSelectedValueCommand.execute(proxy, selectElement, args));
  }
}
