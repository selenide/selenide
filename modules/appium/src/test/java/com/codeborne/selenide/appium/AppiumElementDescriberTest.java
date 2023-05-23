package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumElementDescriber.isUnsupportedAttributeError;
import static com.codeborne.selenide.appium.AppiumElementDescriber.removePackage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppiumElementDescriberTest {
  private final AppiumElementDescriber describer = new AppiumElementDescriber();
  private final Driver driver = mock(Driver.class);
  private final WebElement element = mock(WebElement.class);

  void givenAndroidDriver() {
    when(driver.getWebDriver()).thenReturn(mock(AndroidDriver.class));
  }

  void givenIosDriver() {
    when(driver.getWebDriver()).thenReturn(mock(IOSDriver.class));
  }

  @Test
  public void printsTagName_ifPresent() {
    givenIosDriver();
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("class")).thenThrow(new UnsupportedCommandException());

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage>?</XCUIElementTypeImage>");
    verify(element, never()).getAttribute("class");
  }

  @Test
  public void printsText_ifPresent() {
    givenIosDriver();
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getText()).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage>element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeText() {
    givenIosDriver();
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getText()).thenReturn(null);
    when(element.getAttribute("text")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage>element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeLabel() {
    givenIosDriver();
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("label")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage>element text</XCUIElementTypeImage>");
  }

  @Test
  public void canExtractTextFromAttributeValue() {
    givenIosDriver();
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute("value")).thenReturn("element text");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<XCUIElementTypeImage>element text</XCUIElementTypeImage>");
  }

  @Test
  public void extractsTagNameFromClassName_inAndroid() {
    givenAndroidDriver();
    when(element.getTagName()).thenReturn(null);
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("className")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");

    assertThat(describer.briefly(driver, element))
      .isEqualTo("<TextView>Hello, world</TextView>");
  }

  @Test
  public void fully() {
    givenAndroidDriver();
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("className")).thenReturn("android.widget.TextView");
    when(element.getAttribute("text")).thenReturn("Hello, world");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<TextView className=\"android.widget.TextView\">Hello, world</TextView>");
  }

  @Test
  public void addAllPossibleAttributes() {
    givenAndroidDriver();
    when(element.getAttribute("class")).thenReturn("android.widget.TextView");
    when(element.getAttribute("className")).thenReturn("android.widget.TextView");
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
      .isEqualTo("<TextView" +
        " resource-id=\"com.android.calculator2:id/result\" checked=\"true\"" +
        " content-desc=\"blah\" enabled=\"true\" focused=\"false\"" +
        " package=\"com.android.calculator2\"" +
        " className=\"android.widget.TextView\"" +
        " bounds=\"[0,183][1080,584]\"" +
        " displayed=\"true\">" +
        "Hello, world</TextView>");
  }

  @Test
  public void ignoresErrorsWhenGettingAnyOfAttributes_android() {
    givenAndroidDriver();
    when(element.getAttribute(anyString())).thenThrow(new WebDriverException("Not implemented"));
    doReturn("android.widget.TextView").when(element).getAttribute("class");
    doReturn("android.widget.TextView").when(element).getAttribute("className");
    doReturn("Hello, world").when(element).getAttribute("text");
    doReturn("com.android.calculator2:id/result").when(element).getAttribute("resource-id");
    doReturn("com.android.calculator2").when(element).getAttribute("package");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<TextView" +
        " resource-id=\"com.android.calculator2:id/result\"" +
        " package=\"com.android.calculator2\"" +
        " className=\"android.widget.TextView\">" +
        "Hello, world</TextView>");
  }

  @Test
  public void ignoresErrorsWhenGettingAnyOfAttributes_ios() {
    givenIosDriver();
    when(driver.getWebDriver()).thenReturn(mock(IOSDriver.class));
    when(element.getTagName()).thenReturn("XCUIElementTypeImage");
    when(element.getAttribute(anyString())).thenThrow(new WebDriverException("Not implemented"));
    doReturn("Hello, world").when(element).getAttribute("label");
    doReturn("true").when(element).getAttribute("enabled");

    assertThat(describer.fully(driver, element))
      .isEqualTo("<XCUIElementTypeImage enabled=\"true\">" +
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

  @Test
  void removesPackageFromAndroidClassName() {
    assertThat(removePackage("android.widget.TextView")).isEqualTo("TextView");
    assertThat(removePackage("JustSomeClass")).isEqualTo("JustSomeClass");
  }
}
