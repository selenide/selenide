package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.UnitTest;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SetValueCommandTest extends UnitTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SetValue setValueCommand;
  private SelectOptionByValue mockedSelectByOption;
  private SelectRadio mockedSelectRadio;
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
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
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    SetValue setValue = new SetValue();
    Field selectOptionByValueField = setValue.getClass().getDeclaredField("selectOptionByValue");
    Field selectRadioField = setValue.getClass().getDeclaredField("selectRadio");
    selectOptionByValueField.setAccessible(true);
    selectRadioField.setAccessible(true);
    SelectOptionByValue selectOptionByValue = (SelectOptionByValue) selectOptionByValueField.get(setValue);
    SelectRadio selectRadio = (SelectRadio) selectRadioField.get(setValue);
    assertThat(selectOptionByValue)
      .isNotNull();
    assertThat(selectRadio)
      .isNotNull();
  }

  @Test
  void testExecuteWithSelectTagElement() {
    System.setProperty("selenide.versatileSetValue", "true");
    when(mockedFoundElement.getTagName()).thenReturn("select");
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[]{"value"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testExecuteWithInputTagElement() {
    when(mockedFoundElement.getTagName()).thenReturn("input");
    when(mockedFoundElement.getAttribute("type")).thenReturn("radio");
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[]{"value"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testElementGetClearedWhenArgsTextIsNull() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[]{null});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testElementGetClearedWhenArgsTextIsEmpty() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[]{""});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testElementGetClearedWhenArgsTextIsNotEmpty() {
    WebElement returnedElement = setValueCommand.execute(proxy, locator, new Object[]{"text"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @AfterEach
  void tearDown() {
    System.setProperty("selenide.versatileSetValue", "false");
  }
}
