package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class SelectOptionContainingTextTest {
  SelectOptionContainingText command = new SelectOptionContainingText();

  WebElement element = mock(WebElement.class);
  WebElement option1 = mock(WebElement.class);
  WebElement option2 = mock(WebElement.class);

  SelenideElement proxy = mock(SelenideElement.class);
  WebElementSource select = mock(WebElementSource.class);

  @Before
  public void setUp() {
    doReturn(element).when(select).getWebElement();
    doReturn("locator").when(element).getTagName();
  }

  @Test
  public void selects_firstMatchingOption_forSingleSelect() {
    doReturn("false").when(element).getAttribute("multiple");
    doReturn(asList(option1, option2)).when(element)
        .findElements(
            By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));
        
    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2, never()).click();
  }

  @Test
  public void selects_allMatchingOptions_forMultipleSelect() {
    doReturn("true").when(element).getAttribute("multiple");
    doReturn(asList(option1, option2)).when(element)
        .findElements(
            By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));
        
    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2).click();
  }
}
