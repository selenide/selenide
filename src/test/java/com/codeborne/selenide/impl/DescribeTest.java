package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
}
