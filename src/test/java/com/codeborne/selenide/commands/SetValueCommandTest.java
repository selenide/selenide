package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetValueCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SetValue setValueCommand;
  private SelectOptionByValue mockedSelectByOption;
  private SelectRadio mockedSelectRadio;
  private WebElement mockedFoundElement;


  @Before
  public void setup() {
    System.setProperty("selenide.versatileSetValue", "true");
    mockedSelectByOption = mock(SelectOptionByValue.class);
    mockedSelectRadio = mock(SelectRadio.class);
    setValueCommand = new SetValue(mockedSelectByOption, mockedSelectRadio);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.findAndAssertElementIsVisible()).thenReturn(mockedFoundElement);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SetValue setValue = new SetValue();
    Field selectOptionByValueField = setValue.getClass().getDeclaredField("selectOptionByValue");
    Field selectRadioField = setValue.getClass().getDeclaredField("selectRadio");
    selectOptionByValueField.setAccessible(true);
    selectRadioField.setAccessible(true);
    SelectOptionByValue selectOptionByValue = (SelectOptionByValue) selectOptionByValueField.get(setValue);
    SelectRadio selectRadio = (SelectRadio) selectRadioField.get(setValue);
    assertNotNull(selectOptionByValue);
    assertNotNull(selectRadio);
  }

  @Test
  public void testExecuteWithSelectTagElement() {
    System.setProperty("selenide.versatileSetValue", "true");
    when(mockedFoundElement.getTagName()).thenReturn("select");
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[] {"value"});
    assertEquals(proxy, returnedElement);
  }

  @Test
  public void testExecuteWithInputTagElement() {
    when(mockedFoundElement.getTagName()).thenReturn("input");
    when(mockedFoundElement.getAttribute("type")).thenReturn("radio");
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[] {"value"});
    assertEquals(proxy, returnedElement);
  }

  @Test
  public void testElementGetClearedWhenArgsTextIsNull() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[] {null});
    assertEquals(proxy, returnedElement);
  }

  @Test
  public void testElementGetClearedWhenArgsTextIsEmpty() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[] {""});
    assertEquals(proxy, returnedElement);
  }

  @Test
  public void testElementGetClearedWhenArgsTextIsNotEmpty() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[] {"text"});
    assertEquals(proxy, returnedElement);
  }

  @After
  public void tearDown() {
    System.setProperty("selenide.versatileSetValue", "false");
  }
}
