package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DescribeTest {

  @Test
  void selectorIsReportedAsIs() {
    Assertions.assertEquals("#firstName", Describe.selector(By.cssSelector("#firstName")));
    Assertions.assertEquals("By.id: firstName", Describe.selector(By.id("firstName")));
    Assertions.assertEquals("By.className: firstName", Describe.selector(By.className("firstName")));
    Assertions.assertEquals("By.name: firstName", Describe.selector(By.name("firstName")));
  }

  @Test
  void shortlyForSelenideElementShouldDelegateToOriginalWebElement() {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getTagName()).thenThrow(new StaleElementReferenceException("disappeared"));

    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.toWebElement()).thenReturn(webElement);
    doThrow(new ElementShould(null, null, visible, webElement, null)).when(selenideElement).getTagName();

    Assertions.assertEquals("StaleElementReferenceException: disappeared", Describe.shortly(selenideElement));
  }
}
