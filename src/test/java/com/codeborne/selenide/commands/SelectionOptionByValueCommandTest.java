package com.codeborne.selenide.commands;

import java.util.Collections;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectionOptionByValueCommandTest implements WithAssertions {
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
    when(selectField.getSearchCriteria()).thenReturn("By.tagName{select}");
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
    assertThat(selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new int[]{1}}))
      .isNull();
  }

  @Test
  void selectByValueWhenElementIsNotFound() {
    when(mockedElement.findElements(By.xpath(
      ".//option[@value = " + Quotes.escape(defaultElementValue) + "]")))
      .thenReturn(Collections.emptyList());
    assertThatThrownBy(() -> {
      selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{defaultElementValue}});
    }).isInstanceOf(ElementNotFound.class)
      .hasMessage(String.format("Element not found {By.tagName{select}/option[value:%s]}\nExpected: exist", defaultElementValue));
  }
}
