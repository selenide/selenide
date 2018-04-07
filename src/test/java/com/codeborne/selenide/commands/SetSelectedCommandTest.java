package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetSelectedCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SetSelected setSelectedCommand;
  private WebElement mockedFoundElement;


  @Before
  public void setup() {
    Click mockedClick = mock(Click.class);
    setSelectedCommand = new SetSelected(mockedClick);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SetSelected setSelected = new SetSelected();
    Field clickField = setSelected.getClass().getDeclaredField("click");
    clickField.setAccessible(true);
    Click click = (Click) clickField.get(setSelected);
    assertNotNull(click);
  }

  @Test
  public void testExecuteMethodWhenElementIsNotDisplayed() {
    when(mockedFoundElement.isDisplayed()).thenReturn(false);
    try {
      setSelectedCommand.execute(proxy, locator, new Object[] {true});
    } catch (InvalidStateException exception) {
      assertEquals("Cannot change invisible element", exception.getMessage());
    }
  }

  @Test
  public void testExecuteMethodWhenElementIsNotInput() {
    checkExecuteMethodWhenTypeOfElementIsIncorrect("select");
  }

  @Test
  public void testExecuteMethodWhenElementIsInputNotRadioOrCheckbox() {
    checkExecuteMethodWhenTypeOfElementIsIncorrect("input");
  }

  @Test
  public void testExecuteMethodWhenElementNotOptionReadonlyEnabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", null);
  }

  @Test
  public void testExecuteMethodWhenElementNotOptionNotReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled(null, "true");
  }

  @Test
  public void testExecuteMethodWhenElementNotOptionReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", "true");
  }

  private void checkExecuteMethodWhenTypeOfElementIsIncorrect(String tagName) {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn(tagName);
    when(mockedFoundElement.getAttribute("type")).thenReturn("href");
    try {
      setSelectedCommand.execute(proxy, locator, new Object[] {true});
    } catch (InvalidStateException exception) {
      assertEquals("Only use setSelected on checkbox/option/radio", exception.getMessage());
    }
  }

  private void checkExecuteMethodWhenElementIsReadOnlyOrDisabled(String readOnlyValue, String disabledValue) {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    when(mockedFoundElement.getAttribute("readonly")).thenReturn(readOnlyValue);
    when(mockedFoundElement.getAttribute("disabled")).thenReturn(disabledValue);
    try {
      setSelectedCommand.execute(proxy, locator, new Object[] {true});
    } catch (InvalidStateException exception) {
      assertEquals("Cannot change value of readonly/disabled element", exception.getMessage());
    }
  }

  @Test
  public void testExecuteMethodWhenElementIsSelected() {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    WebElement returnedElement = setSelectedCommand.execute(proxy, locator, new Object[] {true});
    assertEquals(proxy, returnedElement);
  }

}
