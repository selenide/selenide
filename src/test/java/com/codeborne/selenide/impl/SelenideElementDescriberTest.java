package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.Collection;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelenideElementDescriberTest {
  private final SelenideElementDescriber describe = new SelenideElementDescriber();
  private final Driver driver = mock();

  @Test
  void selectorIsReportedAsIs() {
    assertThat(describe.selector(By.id("firstName"))).isEqualTo("By.id: firstName");
    assertThat(describe.selector(By.className("bootstrap-active"))).isEqualTo("By.className: bootstrap-active");
    assertThat(describe.selector(By.name("firstName"))).isEqualTo("By.name: firstName");
    assertThat(describe.selector(By.linkText("tere"))).isEqualTo("By.linkText: tere");
    assertThat(describe.selector(By.partialLinkText("tere"))).isEqualTo("By.partialLinkText: tere");
    assertThat(describe.selector(By.tagName("tere"))).isEqualTo("By.tagName: tere");
    assertThat(describe.selector(By.xpath("tere"))).isEqualTo("By.xpath: tere");
  }

  @Test
  void cssSelectorIsShortened() {
    assertThat(describe.selector(By.cssSelector("#firstName"))).isEqualTo("#firstName");
  }

  @Test
  void shortlyForSelenideElementShouldDelegateToOriginalWebElement() {
    WebElement webElement = mock();
    when(webElement.getTagName()).thenThrow(new StaleElementReferenceException("disappeared"));

    SelenideElement selenideElement = mock();
    when(selenideElement.toWebElement()).thenReturn(webElement);
    doThrow(new ElementShould(driver, Alias.NONE, "div", "", visible, null, webElement, null)).when(selenideElement).getTagName();

    assertThat(describe.briefly(driver, selenideElement))
      .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: disappeared]");
  }

  @Test
  void describe() {
    SelenideElement selenideElement = element("h1", "class", "active");

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 class=\"active\">Hello yo</h1>");
  }

  @Test
  void describe_attribute_with_empty_value() {
    SelenideElement selenideElement = element("input", "readonly", "");

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<input readonly>Hello yo</input>");
  }

  @Test
  void describe_elementHasDisappeared() {
    SelenideElement selenideElement = element("input", "readonly", "");
    doThrow(new StaleElementReferenceException("Booo")).when(selenideElement).getTagName();

    assertThat(describe.fully(driver, selenideElement))
      .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: Booo]");
  }

  @Test
  void describe_collectionHasResized() {
    SelenideElement selenideElement = element("input", "readonly", "");
    doThrow(new IndexOutOfBoundsException("Fooo")).when(selenideElement).getTagName();

    assertThat(describe.fully(driver, selenideElement))
      .isEqualTo("Ups, failed to described the element [caused by: java.lang.IndexOutOfBoundsException: Fooo]");
  }

  @Test
  void describe_appium_NoSuchElementException() {
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("class")).thenThrow(new NoSuchElementException("Appium throws exception for missing attributes"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_UnsupportedOperationException() {
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("disabled")).thenThrow(new UnsupportedOperationException(
      "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_UnsupportedCommandException() {
    SelenideElement selenideElement = element("h1", "name", "theName");
    when(selenideElement.getAttribute("disabled")).thenThrow(new UnsupportedCommandException(
      "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isSelected_UnsupportedOperationException() {
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isSelected()).thenThrow(new UnsupportedOperationException("isSelected doesn't work in iOS"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isSelected_WebDriverException() {
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isSelected()).thenThrow(new WebDriverException("isSelected might fail on stolen element"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>");
  }

  @Test
  void describe_appium_isDisplayed_UnsupportedOperationException() {
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isDisplayed()).thenThrow(new UnsupportedOperationException("it happens"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo(
      "<h1 name=\"fname\" displayed:java.lang.UnsupportedOperationException: it happens>Hello yo</h1>");
  }

  @Test
  void describe_appium_isDisplayed_WebDriverException() {
    SelenideElement selenideElement = element("h1", "name", "fname");
    when(selenideElement.isDisplayed()).thenThrow(new WebDriverException("isDisplayed might fail on stolen element"));

    assertThat(describe.fully(driver, selenideElement)).isEqualTo(
      "<h1 name=\"fname\" displayed:WebDriverException: isDisplayed might fail on stolen element>Hello yo</h1>");
  }

  @Test
  void fully_onNullCollection() {
    assertThat(describe.fully(driver, (Collection<WebElement>) null)).isEqualTo("[not loaded yet...]");
  }

  @Test
  void fully_fetchesCollectionFromWebdriverIfNotFetchedYet() {
    WebElement element1 = mockWebElement("option", "Bruce");
    WebElement element2 = mockWebElement("option", "Robert");

    assertThat(describe.fully(driver, asList(element1, element2))).isEqualToIgnoringNewLines("""
      [
      \t<option>Bruce</option>,
      \t<option>Robert</option>
      ]
      """);
  }

  @Test
  void fully_printsErrorIfFailedToFetchElements() {
    WebElement element1 = mockWebElement("option", "Bruce");
    WebElement element2 = mockWebElement("option", "Robert");
    when(element2.getText()).thenThrow(new WebDriverException("Failed to fetch elements"));
    assertThat(describe.fully(driver, asList(element1, element2))).isEqualToIgnoringNewLines("""
      [
      \t<option>Bruce</option>,
      \t<option>WebDriverException: Failed to fetch elements</option>
      ]
      """);
  }

  private SelenideElement element(String tagName, String attributeName, String attributeValue) {
    SelenideElement selenideElement = mock();
    when(selenideElement.getTagName()).thenReturn(tagName);
    when(selenideElement.getText()).thenReturn("Hello yo");
    when(selenideElement.isDisplayed()).thenReturn(true);
    when(selenideElement.getAttribute(attributeName)).thenReturn(attributeValue);
    return selenideElement;
  }
}
