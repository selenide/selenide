package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumElementDescriber.isUnsupportedAttributeError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppiumElementDescriberTest {
  private final AppiumElementDescriber describer = new AppiumElementDescriber();
  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  @BeforeEach
  void setUp() {
    when(driver.getWebDriver()).thenReturn(mock(AndroidDriver.class));
  }

  @Test
  public void printsTagName_ifPresent() {
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("class")).thenThrow(new UnsupportedCommandException());

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\">?</XCUIElementTypeImage>");
  }

  @Test
  public void printsText_ifPresent() {
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getText()).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\">element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeText() {
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getText()).thenReturn(null);
    when(element.getAttribute("text")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\">element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeLabel() {
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("label")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\">element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeValue() {
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("value")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\">element text</XCUIElementTypeImage>");
  }

  @Test
  public void extractsTagNameFromClassName_inAndroid() {
    when(element.getTagName()).thenReturn(null);
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<TextView class=\"android.widget.TextView\">Hello, world</TextView>");
  }

  @Test
  public void addsElementId() {
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");
    when(element.getAttribute("resource-id")).thenReturn("submitPaymentButton");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<TextView class=\"android.widget.TextView\" resource-id=\"submitPaymentButton\">Hello, world</TextView>");
  }

  @Test
  public void fully() {
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<TextView class=\"android.widget.TextView\">Hello, world</TextView>");
  }

  @Test
  public void addAllPossibleAttributes() {
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");
    when(element.getAttribute("resource-id")).thenReturn("com.android.calculator2:id/result");
    when(element.getAttribute("checked")).thenReturn("true");
    when(element.getAttribute("content-desc")).thenReturn("blah");
    when(element.getAttribute("enabled")).thenReturn("true");
    when(element.getAttribute("focused")).thenReturn("false");
    when(element.getAttribute("package")).thenReturn("com.android.calculator2");
    when(element.getAttribute("bounds")).thenReturn("[0,183][1080,584]");
    when(element.getAttribute("displayed")).thenReturn("true");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<TextView class=\"android.widget.TextView\"" +
        " resource-id=\"com.android.calculator2:id/result\" checked=\"true\"" +
        " content-desc=\"blah\" enabled=\"true\" focused=\"false\"" +
        " package=\"com.android.calculator2\"" +
        " bounds=\"[0,183][1080,584]\"" +
        " displayed=\"true\">" +
        "Hello, world</TextView>");
  }

  @Test
  public void ignoresErrorsWhenGettingAnyOfAttributes_android() {
    when(element.getAttribute(anyString())).thenThrow(new WebDriverException("Not implemented"));
    doReturn("android.widget.TextView").when(element).getAttribute("class");
    doReturn("Hello, world").when(element).getAttribute("text");
    doReturn("com.android.calculator2:id/result").when(element).getAttribute("resource-id");
    doReturn("com.android.calculator2").when(element).getAttribute("package");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<TextView class=\"android.widget.TextView\"" +
        " resource-id=\"com.android.calculator2:id/result\"" +
        " package=\"com.android.calculator2\">" +
        "Hello, world</TextView>");
  }

  @Test
  public void ignoresErrorsWhenGettingAnyOfAttributes_ios() {
    when(driver.getWebDriver()).thenReturn(mock(IOSDriver.class));
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute(anyString())).thenThrow(new WebDriverException("Not implemented"));
    doReturn("Hello, world").when(element).getAttribute("label");
    doReturn("true").when(element).getAttribute("enabled");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<XCUIElementTypeImage class=\"?\" enabled=\"true\">" +
        "Hello, world</XCUIElementTypeImage>");
  }

  @Test
  void isUnsupportedAttributeError_android() {
    assertThat(isUnsupportedAttributeError(new UnsupportedCommandException())).isTrue();
  }

  @Test
  void isUnsupportedAttributeError_ios() {
    WebDriverException error = new WebDriverException("Original error: The attribute 'text' is unknown. Valid attribute names are:");
    assertThat(isUnsupportedAttributeError(error)).isTrue();
  }

  @Test
  void isUnsupportedAttributeError_otherErrors() {
    assertThat(isUnsupportedAttributeError(new WebDriverException("Not implemented"))).isFalse();
    assertThat(isUnsupportedAttributeError(new NoSuchElementException("nope"))).isFalse();
  }
}
