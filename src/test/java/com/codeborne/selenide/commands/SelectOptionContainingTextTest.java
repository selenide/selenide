package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelectOptionContainingTextTest {
  private final SelectOptionContainingText command = new SelectOptionContainingText();

  private final WebElement element = mock(WebElement.class);
  private final WebElement option1 = mock(WebElement.class);
  private final WebElement option2 = mock(WebElement.class);
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource select = mock(WebElementSource.class);

  @BeforeEach
  void setUp() {
    doReturn(element).when(select).getWebElement();
    doReturn("select").when(element).getTagName();
  }

  @Test
  void selectsFirstMatchingOptionForSingleSelect() {
    when(element.getDomAttribute(any())).thenReturn("false");
    when(element.findElements(any())).thenReturn(asList(option1, option2));

    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2, never()).click();
    verify(element).getAttribute("multiple");
    verify(element).findElements(By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));
  }

  @Test
  void selectsAllMatchingOptionsForMultipleSelect() {
    when(element.getDomAttribute(any())).thenReturn("true");
    when(element.findElements(any())).thenReturn(asList(option1, option2));

    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2).click();
    verify(element).getAttribute("multiple");
    verify(element).findElements(By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));
  }

  @Test
  void throwsNoSuchElementExceptionWhenNoElementsFound() {
    assertThatThrownBy(() -> command.execute(proxy, select, new Object[]{"option-subtext"}))
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("Cannot locate option containing text: option-subtext");
  }
}
