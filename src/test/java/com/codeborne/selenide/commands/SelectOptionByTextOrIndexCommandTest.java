package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectOptionByTextOrIndexCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource selectField;
  private SelectOptionByTextOrIndex selectOptionByTextOrIndexCommand;
  private WebElement mockedElement;
  private String defaultElementText = "This is element text";
  private WebElement mockedFoundElement;
  private int defaultIndex = 1;

  @BeforeEach
  void setup() {
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
  void testSelectOptionByText() {
    when(mockedElement.findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(defaultElementText) + "]")))
      .thenReturn(singletonList(mockedFoundElement));
    selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{this.defaultElementText}});
  }

  @Test
  void testSelectOptionByTextWhenElementIsNotFound() {
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{this.defaultElementText}});
    } catch (ElementNotFound exception) {
      assertThat(exception)
        .hasMessage(String.format("Element not found {null/option[text:%s]}\nExpected: exist", this.defaultElementText));
    }
  }

  @Test
  void testSelectedOptionByTextWhenNoSuchElementIsThrown() {
    doThrow(new NoSuchElementException("no element found"))
      .when(mockedElement)
      .findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(defaultElementText) + "]"));
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new String[]{""}});
    } catch (ElementNotFound exception) {
      assertThat(exception)
        .hasMessage("Element not found {null/option[text:]}\nExpected: exist");
    }
  }

  @Test
  void testSelectOptionByIndex() {
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(singletonList(mockedFoundElement));
    when(mockedFoundElement.getAttribute("index")).thenReturn(String.valueOf(defaultIndex));
    selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new int[]{defaultIndex}});
  }

  @Test
  void testSelectOptionByIndexWhenNoElementFound() {
    try {
      selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new int[]{defaultIndex}});
    } catch (ElementNotFound exception) {
      assertThat(exception)
        .hasMessage(String.format("Element not found {null/option[index:%d]}\nExpected: exist", defaultIndex));
    }
  }

  @Test
  void testExecuteMethodWhenArgIsNotStringOrInt() {
    assertThat(selectOptionByTextOrIndexCommand.execute(proxy, selectField, new Object[]{new Double[]{1.0}}))
      .isNull();
  }
}
