package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SetValueCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelectOptionByValue mockedSelectByOption = mock(SelectOptionByValue.class);
  private final SelectRadio mockedSelectRadio = mock(SelectRadio.class);
  private final SetValue setValueCommand = new SetValue(mockedSelectByOption, mockedSelectRadio);
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    System.setProperty("selenide.versatileSetValue", "true");
    when(locator.findAndAssertElementIsInteractable()).thenReturn(mockedFoundElement);
    when(locator.driver()).thenReturn(new DriverStub());
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
