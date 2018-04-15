package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectRadioCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelectRadio selectRadioCommand;
  private String defaultElementValue = "ElementValue";
  private WebElement mockedFoundElement;

  @Before
  public void setup() {
    Click mockedClick = mock(Click.class);
    selectRadioCommand = new SelectRadio(mockedClick);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);

    when(mockedFoundElement.getAttribute("value")).thenReturn(defaultElementValue);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SelectRadio selectRadio = new SelectRadio();
    Field clickField = selectRadio.getClass().getDeclaredField("click");
    clickField.setAccessible(true);
    Click click = (Click) clickField.get(selectRadio);
    assertNotNull(click);
  }

  @Test
  public void testExecuteMethodWhenNoElementsIsFound() {
    when(locator.findAll()).thenReturn(Collections.emptyList());
    try {
      selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    } catch (ElementNotFound exception) {
      assertEquals(String.format("Element not found {null}\n" +
          "Expected: value '%s'", defaultElementValue),
        exception.getMessage());
    }
  }

  @Test
  public void testExecuteMethodWhenRadioButtonIsReadOnly() {
    when(locator.findAll()).thenReturn(Collections.singletonList(mockedFoundElement));
    when(mockedFoundElement.getAttribute("readonly")).thenReturn("true");
    try {
      selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    } catch (InvalidStateException exception) {
      assertEquals("Cannot select readonly radio button", exception.getMessage());
    }
  }

  @Test
  public void testExecuteMethodOnFoundRadioButton() {
    when(locator.findAll()).thenReturn(Collections.singletonList(mockedFoundElement));
    SelenideElement clickedElement = selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    assertEquals(mockedFoundElement, clickedElement.getWrappedElement());
  }
}
