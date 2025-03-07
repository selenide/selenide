package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByShadow;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.SelectorMode.CSS;
import static com.codeborne.selenide.SelectorMode.Sizzle;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class WebElementSelectorTest {

  private final WebElementSelector selector = new WebElementSelector();
  private final Browser browser = new Browser("netscape navigator", false);
  private final JSWebDriver webDriver = mock();
  private final WebElementSource parent = mock();

  @Test
  void findElement_byCss() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    WebElement div = mock();
    when(webDriver.findElement(any())).thenReturn(div);

    assertThat(selector.findElement(driver, null, By.cssSelector("a.active"))).isSameAs(div);

    verify(webDriver).findElement(By.cssSelector("a.active"));
  }

  @Test
  void findElement_byNonCss() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    WebElement div = mock();
    when(webDriver.findElement(any())).thenReturn(div);

    assertThat(selector.findElement(driver, null, By.xpath("/div/h1"))).isSameAs(div);

    verify(webDriver).findElement(By.xpath("/div/h1"));
  }

  @Test
  void findElement_fromRoot_canUseSizzleSelectors() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    WebElement div = mock();
    when(webDriver.executeScript("return typeof Sizzle != 'undefined'")).thenReturn(true);
    when(webDriver.executeScript("return Sizzle(arguments[0])", "a.active:last")).thenReturn(asList(div));

    assertThat(selector.findElement(driver, null, By.cssSelector("a.active:last"))).isSameAs(div);
  }

  @Test
  void findElement_insideElement_canUseSizzleSelectors() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    SelenideElement parentElement = mockElement("div", "the parent");
    when(parent.getWebElement()).thenReturn(parentElement);
    WebElement div = mock();
    when(webDriver.executeScript(anyString())).thenReturn(true);
    when(webDriver.executeScript(anyString(), any(), any())).thenReturn(asList(div));

    assertThat(selector.findElement(driver, parent, By.cssSelector("a.active:last"))).isSameAs(div);

    verify(webDriver).executeScript("return typeof Sizzle != 'undefined'");
    verify(webDriver).executeScript("return Sizzle(arguments[0], arguments[1])", "a.active:last", parentElement);
    verifyNoMoreInteractions(webDriver);
  }

  @Test
  void findElements_byCss() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    List<WebElement> divs = asList(mock(), mock());
    when(webDriver.findElements(any())).thenReturn(divs);

    assertThat(selector.findElements(driver, null, By.cssSelector("a.active"))).isSameAs(divs);

    verify(webDriver).findElements(By.cssSelector("a.active"));
  }

  @Test
  void findElements_byNonCss() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    List<WebElement> divs = asList(mock(), mock());
    when(webDriver.findElements(any())).thenReturn(divs);

    assertThat(selector.findElements(driver, null, By.xpath("/div/h1"))).isSameAs(divs);

    verify(webDriver).findElements(By.xpath("/div/h1"));
  }

  @Test
  void findElement_insideElement_cannotUseXpathStartingWithSlash() {
    Driver driver = new DriverStub();
    SelenideElement parentElement = mockElement("div", "whatever");
    when(parent.getWebElement()).thenReturn(parentElement);

    assertThatThrownBy(() -> selector.findElement(driver, parent, By.xpath("/div")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  @Test
  void findElements_insideElement_cannotUseXpathStartingWithSlash() {
    Driver driver = new DriverStub();
    SelenideElement parentElement = mockElement("div", "whatever");
    when(parent.getWebElement()).thenReturn(parentElement);

    assertThatThrownBy(() -> selector.findElements(driver, parent, By.xpath("/div")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  @Test
  void findElement_withShadowRoot() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    SelenideElement parentElement = mockElement("div", "the parent");
    when(parent.getWebElement()).thenReturn(parentElement);
    when(parent.isShadowRoot()).thenReturn(true);
    when(parentElement.getWrappedDriver()).thenReturn(webDriver);

    SearchContext shadowRoot = mock();
    when(webDriver.executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement)).thenReturn(shadowRoot);

    WebElement div = mock();
    when(shadowRoot.findElement(any())).thenReturn(div);

    assertThat(selector.findElement(driver, parent, By.cssSelector("a.active"))).isSameAs(div);

    verify(webDriver).executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement);
  }

  @Test
  void findElements_withShadowRoot() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    SelenideElement parentElement = mockElement("div", "the parent");
    when(parent.getWebElement()).thenReturn(parentElement);
    when(parent.isShadowRoot()).thenReturn(true);
    when(parentElement.getWrappedDriver()).thenReturn(webDriver);

    SearchContext shadowRoot = mock();
    when(webDriver.executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement)).thenReturn(shadowRoot);

    List<WebElement> divs = asList(mock(), mock());
    when(shadowRoot.findElements(any())).thenReturn(divs);

    assertThat(selector.findElements(driver, parent, By.cssSelector("a.active"))).isSameAs(divs);

    verify(webDriver).executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement);
  }

  @Test
  void findElement_withoutShadowRoot() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    SelenideElement parentElement = mockElement("div", "the parent");
    when(parent.getWebElement()).thenReturn(parentElement);
    when(parent.isShadowRoot()).thenReturn(true);
    when(parentElement.getWrappedDriver()).thenReturn(webDriver);

    when(webDriver.executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement)).thenReturn(null);

    assertThatExceptionOfType(IllegalArgumentException.class)
      .isThrownBy(() -> selector.findElement(driver, parent, By.cssSelector("a.active")))
      .withMessage(String.format("%s does not contain shadow root", parentElement));

    verify(webDriver).executeScript(ByShadow.GET_SHADOW_ROOT_SCRIPT, parentElement);
  }

  interface JSWebDriver extends WebDriver, JavascriptExecutor {

  }
}
