package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelectOptionByTextOrIndexCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectField = mock(WebElementSource.class);
  private final SelectOptionByTextOrIndex command = new SelectOptionByTextOrIndex();
  private final WebElement select = mockWebElement("select", "This is element text");
  private final WebElement option = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(selectField.driver()).thenReturn(new DriverStub());
    when(selectField.getWebElement()).thenReturn(select);
    when(selectField.getSearchCriteria()).thenReturn("select#main");
    when(selectField.getAlias()).thenReturn(Alias.NONE);
    when(option.isSelected()).thenReturn(false);
  }

  @Test
  void selectOptionByText() {
    when(select.findElements(any())).thenReturn(singletonList(option));
    command.execute(proxy, selectField, new Object[]{new String[]{"This is element text"}});
    verify(select).findElements(By.xpath(".//option[normalize-space(.) = \"This is element text\"]"));
  }

  @Test
  void selectOptionByTextWhenElementIsNotFound() {
    assertThatThrownBy(() -> command.execute(proxy, selectField, new Object[]{new String[]{"This is element text"}}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#main/option[text:This is element text]}")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void selectedOptionByTextWhenNoSuchElementIsThrown() {
    doThrow(new NoSuchElementException("no element found")).when(select).findElements(any());
    assertThatThrownBy(() -> command.execute(proxy, selectField, new Object[]{new String[]{"Mega text"}}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#main/option[text:Mega text]}")
      .hasMessageContaining("Expected: exist")
      .hasMessageContaining("Caused by: NoSuchElementException: no element found");
    verify(select).findElements(By.xpath(".//option[normalize-space(.) = \"Mega text\"]"));
  }

  @Test
  void selectOptionByIndex() {
    when(select.findElements(any())).thenReturn(singletonList(option));
    when(option.getAttribute(any())).thenReturn("33");

    command.execute(proxy, selectField, new Object[]{new int[]{33}});

    verify(option).click();
    verify(select).findElements(By.tagName("option"));
    verify(option).getAttribute("index");
  }

  @Test
  void selectOptionByIndexWhenNoElementFound() {
    assertThatThrownBy(() -> command.execute(proxy, selectField, new Object[]{new int[]{42}}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#main/option[index:42]}")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void throwsIllegalArgumentException_whenArgIsNotStringOrInt() {
    assertThatThrownBy(() -> command.execute(proxy, selectField, new Object[]{new Double[]{1.0}}))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
