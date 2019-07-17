package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DescribeTest implements WithAssertions {
  @Test
  void selectorIsReportedAsIs() {
    assertThat(Describe.selector(By.cssSelector("#firstName")))
      .isEqualTo("#firstName");
    assertThat(Describe.selector(By.id("firstName")))
      .isEqualTo("By.id: firstName");
    assertThat(Describe.selector(By.className("firstName")))
      .isEqualTo("By.className: firstName");
    assertThat(Describe.selector(By.name("firstName")))
      .isEqualTo("By.name: firstName");
  }

  @Test
  void shortlyForSelenideElementShouldDelegateToOriginalWebElement() {
    Driver driver = mock(Driver.class);
    WebElement webElement = mock(WebElement.class);
    when(webElement.getTagName()).thenThrow(new StaleElementReferenceException("disappeared"));

    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.toWebElement()).thenReturn(webElement);
    doThrow(new ElementShould(driver, null, null, visible, webElement, null)).when(selenideElement).getTagName();

    assertThat(Describe.shortly(driver, selenideElement))
      .isEqualTo("StaleElementReferenceException: disappeared");
  }

  @Test
  void describe() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.getTagName()).thenReturn("h1");
    when(selenideElement.getText()).thenReturn("Hello yo");
    when(selenideElement.isDisplayed()).thenReturn(true);
    when(selenideElement.getAttribute("class")).thenReturn("active");

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 class=\"active\">Hello yo</h1>");
  }

  @Test
  void describe_appium() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.getTagName()).thenReturn("h1");
    when(selenideElement.getText()).thenReturn("Hello yo");
    when(selenideElement.isDisplayed()).thenReturn(true);
    when(selenideElement.getAttribute("name")).thenReturn("theName");
    when(selenideElement.getAttribute("class")).thenThrow(new NoSuchElementException("Appium throws exception for missing attributes"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }
}
