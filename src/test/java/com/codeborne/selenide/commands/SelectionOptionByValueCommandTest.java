package com.codeborne.selenide.commands;

import java.util.Collections;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectionOptionByValueCommandTest {
  private SelenideElement proxy;
  private WebElementSource selectField;
  private SelectOptionByValue selectOptionByValueCommand;
  private WebElement mockedElement;
  private String defaultElementValue = "ElementValue";
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    selectOptionByValueCommand = new SelectOptionByValue();
    proxy = mock(SelenideElement.class);
    selectField = mock(WebElementSource.class);
    mockedElement = mock(WebElement.class);
    mockedFoundElement = mock(WebElement.class);

    when(selectField.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn(defaultElementValue);
    when(mockedElement.getTagName()).thenReturn("select");
    when(mockedFoundElement.isSelected()).thenReturn(true);
  }

  @Test
  void testSelectByValueWithStringFromArgs() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.singletonList(mockedFoundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{defaultElementValue});
  }

  @Test
  void testSelectByValueWithStringArrayFromArgs() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.singletonList(mockedFoundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{defaultElementValue}});
  }

  @Test
  void testSelectByValueWithArgNotString() {
    Assertions.assertNull(selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new int[]{1}}));
  }

  @Test
  void testSelectByValueWhenElementIsNotFound() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.emptyList());
    try {
      selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{defaultElementValue}});
    } catch (NoSuchElementException exception) {
      Assertions.assertTrue(
        exception.getMessage().contains(
          String.format("Cannot locate option with value: %s", defaultElementValue)),
        "Value is not present in error message");
    }
  }
}
