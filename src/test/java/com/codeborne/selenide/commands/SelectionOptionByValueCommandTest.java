package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import java.util.Collections;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectionOptionByValueCommandTest {
  private SelenideElement proxy;
  private WebElementSource selectField;
  private SelectOptionByValue selectOptionByValueCommand;
  private WebElement mockedElement;
  private String defaultElementValue = "ElementValue";
  private WebElement mockedFoundElement;

  @Before
  public void setup() {
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
  public void testSelectByValueWithStringFromArgs() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.singletonList(mockedFoundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[] {defaultElementValue});
  }

  @Test
  public void testSelectByValueWithStringArrayFromArgs() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.singletonList(mockedFoundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[] {new String[] {defaultElementValue}});
  }

  @Test
  public void testSelectByValueWithArgNotString() {
    assertNull(selectOptionByValueCommand.execute(proxy, selectField, new Object[] {new int[] {1}}));
  }

  @Test
  public void testSelectByValueWhenElementIsNotFound() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.emptyList());
    try {
      selectOptionByValueCommand.execute(proxy, selectField, new Object[] {new String[] {defaultElementValue}});
    } catch (NoSuchElementException exception) {
      assertTrue("Value is not present in error message",
          exception.getMessage().contains(String.format("Cannot locate option with value: %s", defaultElementValue)));
    }
  }
}
