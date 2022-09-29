package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collections;

import static com.codeborne.selenide.Mocks.mockWebElement;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelectionOptionByValueCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectField = mock(WebElementSource.class);
  private final SelectOptionByValue selectOptionByValueCommand = new SelectOptionByValue();
  private final WebElement select = mockWebElement("select", "walue");
  private final WebElement option = mockWebElement("option", "");

  @BeforeEach
  void setup() {
    when(selectField.driver()).thenReturn(new DriverStub());
    when(selectField.getWebElement()).thenReturn(select);
    when(selectField.getAlias()).thenReturn(NONE);
    when(selectField.getSearchCriteria()).thenReturn("By.tagName{select}");
    when(option.isSelected()).thenReturn(true);
  }

  @Test
  void selectByValueWithStringFromArgs() {
    when(select.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(Collections.singletonList(option));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{"walue"});
  }

  @Test
  void selectByValueWithStringArrayFromArgs() {
    when(select.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(Collections.singletonList(option));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{"walue"}});
  }

  @Test
  void selectByValueWithArgNotString() {
    assertThat(selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new int[]{1}}))
      .isNull();
  }

  @Test
  void selectByValueWhenElementIsNotFound() {
    when(select.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(emptyList());

    assertThatThrownBy(() ->
      selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{"walue"}}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(String.format("Element not found {By.tagName{select}/option[value:walue]}%nExpected: exist"));
  }
}
