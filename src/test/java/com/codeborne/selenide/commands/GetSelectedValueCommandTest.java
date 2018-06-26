package com.codeborne.selenide.commands;

import java.io.IOException;
import java.lang.reflect.Field;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSelectedValueCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource selectElement = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetSelectedValue getSelectedValueCommand;
  private GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);

  @BeforeEach
  void setup() {
    getSelectedValueCommand = new GetSelectedValue(getSelectedOptionCommand);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetSelectedValue getSelectedText = new GetSelectedValue();
    Field getSelectedOptionField = getSelectedText.getClass().getDeclaredField("getSelectedOption");
    getSelectedOptionField.setAccessible(true);
    GetSelectedOption getSelectedOption = (GetSelectedOption) getSelectedOptionField.get(getSelectedText);
    Assertions.assertNotNull(getSelectedOption);
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsNothing() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(null);
    Assertions.assertNull(getSelectedValueCommand.execute(proxy, selectElement, args));
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsElement() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getAttribute("value")).thenReturn(elementText);
    Assertions.assertEquals(elementText, getSelectedValueCommand.execute(proxy, selectElement, args));
  }
}
