package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
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
      .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: disappeared]");
  }

  @Test
  void describe() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "class", "active");

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 class=\"active\">Hello yo</h1>");
  }

  @Test
  void describe_attribute_with_empty_value() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("input", "readonly", "");

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<input readonly>Hello yo</input>");
  }

  @Test
  void describe_elementHasDisappeared() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("input", "readonly", "");
    doThrow(new StaleElementReferenceException("Booo")).when(selenideElement).getTagName();

    assertThat(Describe.describe(driver, selenideElement))
      .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: Booo]");
  }

  @Test
  void describe_collectionHasResized() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("input", "readonly", "");
    doThrow(new IndexOutOfBoundsException("Fooo")).when(selenideElement).getTagName();

    assertThat(Describe.describe(driver, selenideElement))
      .isEqualTo("Ups, failed to described the element [caused by: java.lang.IndexOutOfBoundsException: Fooo]");
  }

  @Test
  void describe_appium_NoSuchElementException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("class")).thenThrow(new NoSuchElementException("Appium throws exception for missing attributes"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_UnsupportedOperationException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("disabled")).thenThrow(new UnsupportedOperationException(
      "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_UnsupportedCommandException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("disabled")).thenThrow(new UnsupportedCommandException(
      "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isSelected_UnsupportedOperationException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isSelected()).thenThrow(new UnsupportedOperationException("isSelected doesn't work in iOS"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isSelected_WebDriverException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isSelected()).thenThrow(new WebDriverException("isSelected might fail on stolen element"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isDisplayed_UnsupportedOperationException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isDisplayed()).thenThrow(new UnsupportedOperationException("it happens"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo(
      "<h1 name=\"fname\" displayed:java.lang.UnsupportedOperationException: it happens>Hello yo</h1>");
  }

  @Test
  void describe_appium_isDisplayed_WebDriverException() {
    Driver driver = mock(Driver.class);
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isDisplayed()).thenThrow(new WebDriverException("isDisplayed might fail on stolen element"));

    assertThat(Describe.describe(driver, selenideElement)).isEqualTo(
      "<h1 name=\"fname\" displayed:WebDriverException: isDisplayed might fail on stolen element>Hello yo</h1>");
  }

  private SelenideElement element(String tagName, String attributeName, String attributeValue) {
    SelenideElement selenideElement = mock(SelenideElement.class);
    when(selenideElement.getTagName()).thenReturn(tagName);
    when(selenideElement.getText()).thenReturn("Hello yo");
    when(selenideElement.isDisplayed()).thenReturn(true);
    when(selenideElement.getAttribute(attributeName)).thenReturn(attributeValue);
    return selenideElement;
  }
}
