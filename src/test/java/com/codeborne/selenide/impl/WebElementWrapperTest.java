package com.codeborne.selenide.impl;

import com.codeborne.selenide.extension.MockWebDriverExtension;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.HTMLUNIT;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockWebDriverExtension.class)
class WebElementWrapperTest implements WithAssertions {
  private WebElement element = createWebElement();

  private WebElement createWebElement() {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn("h2");
    when(element.toString()).thenReturn("webElement");
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    when(element.getAttribute("id")).thenReturn("id1");
    when(element.getAttribute("class")).thenReturn("class1 class2");
    return element;
  }

  @BeforeEach
  final void mockWebDriver() {
    browser = null;
  }

  @Test
  void toStringPrintsTagNameWithAllAttributes() {
    browser = CHROME;
    when(webdriverContainer.hasWebDriverStarted()).thenReturn(true);
    when(webdriverContainer.getWebDriver()).thenReturn(mock(FirefoxDriver.class));
    when(((JavascriptExecutor) webdriverContainer.getWebDriver())
      .executeScript(anyString(), any()))
      .thenReturn(ImmutableMap.of("id", "id1", "class", "class1 class2", "data-binding", "to-name"));

    assertThat(new WebElementWrapper(element))
      .hasToString("<h2 class=\"class1 class2\" data-binding=\"to-name\" id=\"id1\"></h2>");
  }

  @Test
  void toStringPrintsTagNameWithSomeAttributes() {
    browser = HTMLUNIT;
    when(webdriverContainer.getWebDriver()).thenReturn(mock(HtmlUnitDriver.class));

    assertThat(new WebElementWrapper(element))
      .hasToString("<h2 class=\"class1 class2\" id=\"id1\"></h2>");
  }

  @Test
  void toStringFallbacksToMinimalImplementation_ifFailedToCallJavaScript() {
    browser = CHROME;
    when(webdriverContainer.hasWebDriverStarted()).thenReturn(true);
    when(webdriverContainer.getWebDriver()).thenReturn(mock(FirefoxDriver.class));
    when(((JavascriptExecutor) webdriverContainer.getWebDriver())
      .executeScript(anyString(), any()))
      .thenThrow(new UnsupportedOperationException("You must be using WebDriver that supports executing javascript"));

    assertThat(new WebElementWrapper(element))
      .hasToString("<h2 class=\"class1 class2\" id=\"id1\"></h2>");
  }
}
