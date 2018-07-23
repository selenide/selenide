package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SetSelectedCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SetSelected setSelectedCommand;
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    Click mockedClick = mock(Click.class);
    setSelectedCommand = new SetSelected(mockedClick);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SetSelected setSelected = new SetSelected();
    Field clickField = setSelected.getClass().getDeclaredField("click");
    clickField.setAccessible(true);
    Click click = (Click) clickField.get(setSelected);
    assertThat(click)
      .isNotNull();
  }

  @Test
  void testExecuteMethodWhenElementIsNotDisplayed() {
    when(mockedFoundElement.isDisplayed()).thenReturn(false);
    try {
      setSelectedCommand.execute(proxy, locator, new Object[]{true});
    } catch (InvalidStateException exception) {
      assertThat(exception)
        .hasMessage("Cannot change invisible element");
    }
  }

  @Test
  void testExecuteMethodWhenElementIsNotInput() {
    checkExecuteMethodWhenTypeOfElementIsIncorrect("select");
  }

  private void checkExecuteMethodWhenTypeOfElementIsIncorrect(String tagName) {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn(tagName);
    when(mockedFoundElement.getAttribute("type")).thenReturn("href");
    try {
      setSelectedCommand.execute(proxy, locator, new Object[]{true});
    } catch (InvalidStateException exception) {
      assertThat(exception)
        .hasMessage("Only use setSelected on checkbox/option/radio");
    }
  }

  @Test
  void testExecuteMethodWhenElementIsInputNotRadioOrCheckbox() {
    checkExecuteMethodWhenTypeOfElementIsIncorrect("input");
  }

  @Test
  void testExecuteMethodWhenElementNotOptionReadonlyEnabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", null);
  }

  private void checkExecuteMethodWhenElementIsReadOnlyOrDisabled(String readOnlyValue, String disabledValue) {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    when(mockedFoundElement.getAttribute("readonly")).thenReturn(readOnlyValue);
    when(mockedFoundElement.getAttribute("disabled")).thenReturn(disabledValue);
    try {
      setSelectedCommand.execute(proxy, locator, new Object[]{true});
    } catch (InvalidStateException exception) {
      assertThat(exception)
        .hasMessage("Cannot change value of readonly/disabled element");
    }
  }

  @Test
  void testExecuteMethodWhenElementNotOptionNotReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled(null, "true");
  }

  @Test
  void testExecuteMethodWhenElementNotOptionReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", "true");
  }

  @Test
  void testExecuteMethodWhenElementIsSelected() {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    WebElement returnedElement = setSelectedCommand.execute(proxy, locator, new Object[]{true});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
