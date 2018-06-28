package com.codeborne.selenide.commands;

import java.lang.reflect.Field;
import java.util.Collections;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectRadioCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelectRadio selectRadioCommand;
  private String defaultElementValue = "ElementValue";
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    Click mockedClick = mock(Click.class);
    selectRadioCommand = new SelectRadio(mockedClick);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);

    when(mockedFoundElement.getAttribute("value")).thenReturn(defaultElementValue);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SelectRadio selectRadio = new SelectRadio();
    Field clickField = selectRadio.getClass().getDeclaredField("click");
    clickField.setAccessible(true);
    Click click = (Click) clickField.get(selectRadio);
    assertThat(click)
      .isNotNull();
  }

  @Test
  void testExecuteMethodWhenNoElementsIsFound() {
    when(locator.findAll()).thenReturn(Collections.emptyList());
    try {
      selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    } catch (ElementNotFound exception) {
      assertThat(exception)
        .hasMessage(String.format("Element not found {null}\nExpected: value '%s'", defaultElementValue));
    }
  }

  @Test
  void testExecuteMethodWhenRadioButtonIsReadOnly() {
    when(locator.findAll()).thenReturn(Collections.singletonList(mockedFoundElement));
    when(mockedFoundElement.getAttribute("readonly")).thenReturn("true");
    try {
      selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    } catch (InvalidStateException exception) {
      assertThat(exception)
        .hasMessage("Cannot select readonly radio button");
    }
  }

  @Test
  void testExecuteMethodOnFoundRadioButton() {
    when(locator.findAll()).thenReturn(Collections.singletonList(mockedFoundElement));
    SelenideElement clickedElement = selectRadioCommand.execute(proxy, locator, new Object[]{defaultElementValue});
    assertThat(clickedElement.getWrappedElement())
      .isEqualTo(mockedFoundElement);
  }
}
