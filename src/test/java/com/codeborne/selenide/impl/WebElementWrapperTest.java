package com.codeborne.selenide.impl;

import com.codeborne.selenide.rules.MockWebdriverContainer;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebElementWrapperTest {
  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();

  WebElement element = createWebElement();

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

  @Before
  public final void mockWebDriver() {
    browser = null;
  }

  @Test
  public void toStringPrintsTagNameWithAllAttributes() {
    browser = CHROME;
    when(webdriverContainer.hasWebDriverStarted()).thenReturn(true);
    when(webdriverContainer.getWebDriver()).thenReturn(mock(FirefoxDriver.class));
    when(((JavascriptExecutor) webdriverContainer.getWebDriver())
        .executeScript(anyString(), any(WebElement.class)))
        .thenReturn(ImmutableMap.of("id", "id1", "class", "class1 class2", "data-binding", "to-name"));

    assertEquals("<h2 class=\"class1 class2\" data-binding=\"to-name\" id=\"id1\"></h2>",
        new WebElementWrapper(element).toString());
  }

  @Test
  public void toStringPrintsTagNameWithSomeAttributes() {
    browser = HTMLUNIT;
    when(webdriverContainer.getWebDriver()).thenReturn(mock(HtmlUnitDriver.class));
    assertEquals("<h2 class=\"class1 class2\" id=\"id1\"></h2>", new WebElementWrapper(element).toString());
  }
}
