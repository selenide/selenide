package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class WebElementWrapperTest implements WithAssertions {
  private SelenideConfig config = new SelenideConfig();
  private WebDriver webDriver = mock(FirefoxDriver.class);
  private Driver driver = new DriverStub(config, new Browser("firefox", false), webDriver, null);
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

  @Test
  void toStringPrintsTagNameWithAllAttributes() {
    config.browser("chrome");
    Map<String, String> map = new HashMap<>();
    map.put("id", "id1");
    map.put("class", "class1 class2");
    map.put("data-binding", "to-name");
    when(((JavascriptExecutor) webDriver)
      .executeScript(anyString(), any()))
      .thenReturn(map);

    assertThat(new WebElementWrapper(driver, element))
      .hasToString("<h2 class=\"class1 class2\" data-binding=\"to-name\" id=\"id1\"></h2>");
  }

  @Test
  void toStringFallbacksToMinimalImplementation_ifFailedToCallJavaScript() {
    when(((JavascriptExecutor) webDriver)
      .executeScript(anyString(), any()))
      .thenThrow(new UnsupportedOperationException("You must be using WebDriver that supports executing javascript"));

    assertThat(new WebElementWrapper(driver, element))
      .hasToString("<h2 class=\"class1 class2\" id=\"id1\"></h2>");
  }
}
