package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DescribeTest {

  @Test
  public void selectorIsReportedAsIs() {
    assertEquals("#firstName", Describe.selector(By.cssSelector("#firstName")));
    assertEquals("By.id: firstName", Describe.selector(By.id("firstName")));
    assertEquals("By.className: firstName", Describe.selector(By.className("firstName")));
    assertEquals("By.name: firstName", Describe.selector(By.name("firstName")));
  }

  @Test
  public void shortlyForSelenideElementShouldDelegateToOriginalWebElement() {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getTagName()).thenThrow(new StaleElementReferenceException("disappeared"));
    
    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.toWebElement()).thenReturn(webElement);
    doThrow(new ElementShould(null, null, visible, webElement, null, 0)).when(selenideElement).getTagName();
    
    assertEquals("StaleElementReferenceException: disappeared", Describe.shortly(selenideElement));
  }
}