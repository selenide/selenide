package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectOptionByTextOrIndexCommandTest {
  private SelenideElement proxy;
  private WebElementSource selectField;
  private SelectOptionByTextOrIndex selectOptionByTextOrIndexCommand;
  private WebElement mockedElement;
  private String defaultElementText = "This is element text";
  private WebElement mockedFoundElement;
  private int defaultIndex = 1;

  @Before
  public void setup() {
    selectOptionByTextOrIndexCommand = new SelectOptionByTextOrIndex();
    proxy = mock(SelenideElement.class);
    selectField = mock(WebElementSource.class);
    mockedElement = mock(WebElement.class);
    mockedFoundElement = mock(WebElement.class);

    when(selectField.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.getText()).thenReturn(defaultElementText);
    when(mockedElement.getTagName()).thenReturn("select");

    when(mockedFoundElement.isSelected()).thenReturn(true);
  }

  @Test
  public void testSelectOptionByText() {
    when(mockedElement.findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(defaultElementText) + "]")))
      .thenReturn(asList(mockedFoundElement));
    selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{this.defaultElementText}});
  }

  @Test
  public void testSelectOptionByTextWhenElementIsNotFound() {
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{this.defaultElementText}});
    } catch (ElementNotFound exception) {
      assertEquals(String.format("Element not found {null/option[text:%s]}\n" +
        "Expected: exist", this.defaultElementText), exception.getMessage());
    }
  }

  @Test
  public void testSelectedOptionByTextWhenNoSuchElementIsThrown() {
    doThrow(new NoSuchElementException("no element found"))
      .when(mockedElement)
      .findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(defaultElementText) + "]"));
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{""}});
    } catch (ElementNotFound exception) {
      assertEquals(String.format("Element not found {null/option[text:%s]}\n" +
        "Expected: exist", ""), exception.getMessage());
    }
  }

  @Test
  public void testSelectOptionByIndex() {
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(Collections.singletonList(mockedFoundElement));
    when(mockedFoundElement.getAttribute("index")).thenReturn(String.valueOf(defaultIndex));
    selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new int[]{defaultIndex}});
  }

  @Test
  public void testSelectOptionByIndexWhenNoElementFound() {
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new int[]{defaultIndex}});
    } catch (ElementNotFound exception) {
      assertEquals(String.format("Element not found {null/option[index:%d]}\n" +
        "Expected: exist", defaultIndex), exception.getMessage());
    }
  }

  @Test
  public void testExecuteMethodWhenArgIsNotStringOrInt() {
    assertNull(selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new Double[]{1.0}}));
  }
}
