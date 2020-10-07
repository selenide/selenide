package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SetSelectedCommandTest implements WithAssertions {
  private final Click mockedClick = mock(Click.class);
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SetSelected setSelectedCommand = new SetSelected(mockedClick);
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(new DriverStub());
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void defaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SetSelected setSelected = new SetSelected();
    Field clickField = setSelected.getClass().getDeclaredField("click");
    clickField.setAccessible(true);
    Click click = (Click) clickField.get(setSelected);
    assertThat(click)
      .isNotNull();
  }

  @Test
  void executeMethodWhenElementIsNotDisplayed() {
    when(mockedFoundElement.isDisplayed()).thenReturn(false);
    try {
      setSelectedCommand.execute(proxy, locator, new Object[]{true});
    } catch (InvalidStateException exception) {
      assertThat(exception)
        .hasMessageStartingWith("Invalid element state: Cannot change invisible element");
    }
  }

  @Test
  void executeMethodWhenElementIsNotInput() {
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
        .hasMessageStartingWith("Invalid element state: Only use setSelected on checkbox/option/radio");
    }
  }

  @Test
  void executeMethodWhenElementIsInputNotRadioOrCheckbox() {
    checkExecuteMethodWhenTypeOfElementIsIncorrect("input");
  }

  @Test
  void executeMethodWhenElementNotOptionReadonlyEnabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", null);
  }

  private void checkExecuteMethodWhenElementIsReadOnlyOrDisabled(String readOnlyValue, String disabledValue) {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    when(mockedFoundElement.getAttribute("readonly")).thenReturn(readOnlyValue);
    when(mockedFoundElement.getAttribute("disabled")).thenReturn(disabledValue);
    assertThatThrownBy(() ->
      setSelectedCommand.execute(proxy, locator, new Object[]{true})
    )
      .isInstanceOf(InvalidStateException.class)
      .hasMessageStartingWith("Invalid element state: Cannot change value of readonly/disabled element");
  }

  @Test
  void executeMethodWhenElementNotOptionNotReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled(null, "true");
  }

  @Test
  void executeMethodWhenElementNotOptionReadonlyDisabled() {
    checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", "true");
  }

  @Test
  void executeMethodWhenElementIsSelected() {
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    when(mockedFoundElement.getTagName()).thenReturn("option");
    WebElement returnedElement = setSelectedCommand.execute(proxy, locator, new Object[]{true});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
