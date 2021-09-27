package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class SetValueCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelectOptionByValue selectOptionByValue = mock(SelectOptionByValue.class);
  private final SelectRadio selectRadio = mock(SelectRadio.class);
  private final SetValue command = new SetValue(selectOptionByValue, selectRadio);
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    System.setProperty("selenide.versatileSetValue", "true");
    when(locator.findAndAssertElementIsInteractable()).thenReturn(mockedFoundElement);
    when(locator.driver()).thenReturn(new DriverStub());
  }

  @AfterEach
  void noMoreInteractions() {
    verify(locator).driver();
    verify(locator).findAndAssertElementIsInteractable();
    verify(mockedFoundElement).getTagName();
    verifyNoMoreInteractions(proxy, locator, selectOptionByValue, selectRadio, mockedFoundElement);
  }

  @Test
  void selectOptionByValue_inCaseOfSelectElement() {
    System.setProperty("selenide.versatileSetValue", "true");
    when(mockedFoundElement.getTagName()).thenReturn("select");
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{"new value"});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(selectOptionByValue).execute(proxy, locator, new Object[]{"new value"});
  }

  @Test
  void selectInputByValue_inCaseOfRadioElement() {
    when(mockedFoundElement.getTagName()).thenReturn("input");
    when(mockedFoundElement.getAttribute("type")).thenReturn("radio");
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{"new radio value"});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(selectRadio).execute(proxy, locator, new Object[]{"new radio value"});
    verify(mockedFoundElement).getAttribute("type");
  }

  @Test
  void clearsTheInputIfArgsTextIsNull() {
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{null});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(mockedFoundElement).clear();
  }

  @Test
  void clearsTheInputIfArgsTextIsEmpty() {
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{""});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(mockedFoundElement).clear();
  }

  @Test
  void typesGivenTextIntoInputField() {
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{"Stalker"});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(mockedFoundElement).clear();
    verify(mockedFoundElement).sendKeys("Stalker");
  }

  @AfterEach
  void tearDown() {
    System.clearProperty("selenide.versatileSetValue");
  }
}
