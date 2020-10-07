package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.SelectorMode.CSS;
import static com.codeborne.selenide.SelectorMode.Sizzle;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class WebElementSelectorTest {
  private WebElementSelector selector = new WebElementSelector();
  private Browser browser = new Browser("zopera", false);
  private JSWebDriver webDriver = mock(JSWebDriver.class);
  private SearchContext parent = mock(WebElement.class);

  @Test
  void findElement_byCss() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    WebElement div = mock(WebElement.class);
    when(webDriver.findElement(By.cssSelector("a.active"))).thenReturn(div);

    assertThat(selector.findElement(driver, webDriver, By.cssSelector("a.active"))).isSameAs(div);
  }

  @Test
  void findElement_byNonCss() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    WebElement div = mock(WebElement.class);
    when(webDriver.findElement(By.xpath("/div/h1"))).thenReturn(div);

    assertThat(selector.findElement(driver, webDriver, By.xpath("/div/h1"))).isSameAs(div);
  }

  @Test
  void findElement_fromRoot_canUseSizzleSelectors() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    WebElement div = mock(WebElement.class);
    when(webDriver.executeScript("return typeof Sizzle != 'undefined'")).thenReturn(true);
    when(webDriver.executeScript("return Sizzle(arguments[0])", "a.active:last")).thenReturn(asList(div));

    assertThat(selector.findElement(driver, driver.getWebDriver(), By.cssSelector("a.active:last"))).isSameAs(div);
  }

  @Test
  void findElement_insideElement_canUseSizzleSelectors() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);

    WebElement div = mock(WebElement.class);
    when(webDriver.executeScript("return typeof Sizzle != 'undefined'")).thenReturn(true);
    when(webDriver.executeScript("return Sizzle(arguments[0], arguments[1])", "a.active:last", parent)).thenReturn(asList(div));

    assertThat(selector.findElement(driver, parent, By.cssSelector("a.active:last"))).isSameAs(div);
  }


  @Test
  void findElements_byCss() {
    Config config = new SelenideConfig().selectorMode(CSS);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    List<WebElement> divs = asList(mock(WebElement.class), mock(WebElement.class));
    when(webDriver.findElements(By.cssSelector("a.active"))).thenReturn(divs);

    assertThat(selector.findElements(driver, webDriver, By.cssSelector("a.active"))).isSameAs(divs);
  }

  @Test
  void findElements_byNonCss() {
    Config config = new SelenideConfig().selectorMode(Sizzle);
    Driver driver = new DriverStub(config, browser, webDriver, null);
    List<WebElement> divs = asList(mock(WebElement.class), mock(WebElement.class));
    when(webDriver.findElements(By.xpath("/div/h1"))).thenReturn(divs);

    assertThat(selector.findElements(driver, webDriver, By.xpath("/div/h1"))).isSameAs(divs);
  }

  @Test
  void findElement_insideElement_cannotUseXpathStartingWithSlash() {
    Driver driver = new DriverStub("zopera");

    assertThatThrownBy(() -> selector.findElement(driver, parent, By.xpath("/div")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  @Test
  void findElements_insideElement_cannotUseXpathStartingWithSlash() {
    Driver driver = new DriverStub("zopera");

    assertThatThrownBy(() -> selector.findElements(driver, parent, By.xpath("/div")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("XPath starting from / searches from root");
  }

  interface JSWebDriver extends WebDriver, JavascriptExecutor {
  }
}
