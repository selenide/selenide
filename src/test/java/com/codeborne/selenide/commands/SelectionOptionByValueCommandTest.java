package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelectionOptionByValueCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectField = mock(WebElementSource.class);
  private final SelectOptionByValue selectOptionByValueCommand = new SelectOptionByValue();
  private final WebElement element = mock(WebElement.class);
  private final WebElement foundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(selectField.driver()).thenReturn(new DriverStub());
    when(selectField.getWebElement()).thenReturn(element);
    when(selectField.getSearchCriteria()).thenReturn("By.tagName{select}");
    when(element.getText()).thenReturn("walue");
    when(element.getTagName()).thenReturn("select");
    when(foundElement.isSelected()).thenReturn(true);
  }

  @Test
  void selectByValueWithStringFromArgs() {
    when(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(Collections.singletonList(foundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{"walue"});
  }

  @Test
  void selectByValueWithStringArrayFromArgs() {
    when(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(Collections.singletonList(foundElement));
    selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{"walue"}});
  }

  @Test
  void selectByValueWithArgNotString() {
    assertThat(selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new int[]{1}}))
      .isNull();
  }

  @Test
  void selectByValueWhenElementIsNotFound() {
    when(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
      .thenReturn(emptyList());

    assertThatThrownBy(() ->
      selectOptionByValueCommand.execute(proxy, selectField, new Object[]{new String[]{"walue"}}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(String.format("Element not found {By.tagName{select}/option[value:walue]}%nExpected: exist"));
  }
}
