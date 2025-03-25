package com.codeborne.selenide.selector;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ByShadowTest {

  private static final String GET_SHADOW_ROOT_SCRIPT = "return arguments[0].shadowRoot";

  private static WebDriver mockDriver() {
    return mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
  }

  private static WebElement mockElement() {
    return mock(WebElement.class, withSettings().extraInterfaces(WrapsDriver.class));
  }

  private static SearchContext getShadowRoot(WebDriver driver, WebElement element) {
    return (SearchContext) ((JavascriptExecutor) driver).executeScript(GET_SHADOW_ROOT_SCRIPT, element);
  }

  @Test
  void findElement_withShadowHost() {
    WebDriver driver = mockDriver();
    WebElement elementWithShadowRoot = mockElement();

    By shadowHost = By.cssSelector("fruit");
    when(driver.findElements(shadowHost)).thenReturn(singletonList(elementWithShadowRoot));
    when(((WrapsDriver) elementWithShadowRoot).getWrappedDriver()).thenReturn(driver);

    SearchContext shadowRoot = mock();
    when(getShadowRoot(driver, elementWithShadowRoot)).thenReturn(shadowRoot);

    By target = By.cssSelector("cheese");
    WebElement result = mock();
    when(shadowRoot.findElements(target)).thenReturn(singletonList(result));

    ByShadow byShadow = new ByShadow(target, shadowHost);
    assertThat(byShadow.findElement(driver)).isSameAs(result);
    assertThat(byShadow).hasToString(String.format("By.shadow: { %s -> %s }", shadowHost, target));
  }

  @Test
  void findElement_withInnerShadowHost() {
    WebDriver driver = mockDriver();
    WebElement elementWithShadowRoot = mockElement();

    By shadowHost = By.cssSelector("fruit");
    when(driver.findElements(shadowHost)).thenReturn(singletonList(elementWithShadowRoot));
    when(((WrapsDriver) elementWithShadowRoot).getWrappedDriver()).thenReturn(driver);

    SearchContext shadowRoot = mock();
    when(getShadowRoot(driver, elementWithShadowRoot)).thenReturn(shadowRoot);

    By innerShadowHost = By.cssSelector("beer");
    WebElement innerElementWithShadowRoot = mockElement();
    when(shadowRoot.findElements(innerShadowHost)).thenReturn(singletonList(innerElementWithShadowRoot));
    when(((WrapsDriver) innerElementWithShadowRoot).getWrappedDriver()).thenReturn(driver);

    SearchContext innerShadowRoot = mock();
    when(getShadowRoot(driver, innerElementWithShadowRoot)).thenReturn(innerShadowRoot);

    By target = By.cssSelector("cheese");
    WebElement result = mock();
    when(innerShadowRoot.findElements(target)).thenReturn(singletonList(result));

    ByShadow byShadow = new ByShadow(target, shadowHost, innerShadowHost);
    assertThat(byShadow.findElement(driver)).isSameAs(result);
    assertThat(byShadow).hasToString(String.format("By.shadow: { %s -> %s -> %s }", shadowHost, innerShadowHost, target));
  }

  @Test
  void findElements_withShadowHost() {
    WebDriver driver = mockDriver();
    WebElement elementWithShadowRoot = mockElement();

    By shadowHost = By.cssSelector("fruit");
    when(driver.findElements(shadowHost)).thenReturn(singletonList(elementWithShadowRoot));
    when(((WrapsDriver) elementWithShadowRoot).getWrappedDriver()).thenReturn(driver);

    SearchContext shadowRoot = mock();
    when(getShadowRoot(driver, elementWithShadowRoot)).thenReturn(shadowRoot);

    By target = By.cssSelector("cheese");
    List<WebElement> result = asList(mock(), mock());
    when(shadowRoot.findElements(target)).thenReturn(result);

    ByShadow byShadow = new ByShadow(target, shadowHost);
    assertThat(byShadow.findElements(driver)).containsAnyElementsOf(result);
    assertThat(byShadow).hasToString(String.format("By.shadow: { %s -> %s }", shadowHost, target));
  }

  @Test
  void findElements_oneElementWithoutShadowRoot() {
    WebDriver driver = mockDriver();
    WebElement elementWithShadowRoot = mockElement();
    WebElement elementWithoutShadowRoot = mockElement();

    By shadowHost = By.cssSelector("fruit");
    when(driver.findElements(shadowHost)).thenReturn(asList(elementWithShadowRoot, elementWithoutShadowRoot));
    when(((WrapsDriver) elementWithShadowRoot).getWrappedDriver()).thenReturn(driver);
    when(((WrapsDriver) elementWithoutShadowRoot).getWrappedDriver()).thenReturn(driver);

    SearchContext shadowRoot = mock();
    when(getShadowRoot(driver, elementWithShadowRoot)).thenReturn(shadowRoot);
    when(getShadowRoot(driver, elementWithoutShadowRoot)).thenReturn(null);

    By target = By.cssSelector("cheese");
    List<WebElement> result = asList(mock(), mock());
    when(shadowRoot.findElements(target)).thenReturn(result);

    ByShadow byShadow = new ByShadow(target, shadowHost);
    assertThat(byShadow.findElements(driver)).containsAnyElementsOf(result);
    assertThat(byShadow).hasToString(String.format("By.shadow: { %s -> %s }", shadowHost, target));
  }
}
