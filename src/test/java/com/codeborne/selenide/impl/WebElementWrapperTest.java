package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class WebElementWrapperTest {
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webDriver = mock(FirefoxDriver.class);
  private final Driver driver = new DriverStub(config, new Browser("firefox", false), webDriver, null);
  private final WebElement element = createWebElement();

  private static WebElement createWebElement() {
    WebElement element = mock();
    when(element.getTagName()).thenReturn("h2");
    when(element.toString()).thenReturn("webElement");
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    when(element.getAttribute("id")).thenReturn("id1");
    when(element.getAttribute("class")).thenReturn("class1 class2");
    return element;
  }

  @Test
  void toString_chrome() {
    when(element.toString()).thenReturn("[[ChromeDriver: chrome on mac (2c8a03051b8f1cb7223ea944947460fc)] -> name: domain]");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{name: domain}");
  }

  @Test
  void toString_chrome_xpath() {
    when(element.toString()).thenReturn("[[ChromeDriver: chrome on mac (39e72c8...896afef)] -> xpath: //*[@name='domain']]");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{xpath: //*[@name='domain']}");
  }

  @Test
  void toString_edge() {
    when(element.toString()).thenReturn("[[EdgeDriver: MicrosoftEdge on mac (1adc8dc1a31b5b7a5d0d0084967c8881)] -> name: domain]");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{name: domain}");
  }

  @Test
  void toString_firefox() {
    when(element.toString()).thenReturn("[[FirefoxDriver: firefox on mac (76d4bbf3-542a-4bf3-848f-98b2e1645e2b)] -> name: domain]");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{name: domain}");
  }

  @Test
  void toString_safari() {
    when(element.toString()).thenReturn("[[SafariDriver: Safari on mac (B14E03E3-9AE3-4906-93C0-1A82F4347669)] -> name: domain]");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{name: domain}");
  }

  @Test
  void toString_decorated() {
    when(element.toString()).thenReturn("Decorated {[[ChromeDriver: chrome on mac (d3340...e2c)] -> tag name: h2]}");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{tag name: h2}");
  }

  @Test
  void toString_decorated_xpath() {
    when(element.toString()).thenReturn("Decorated {[[ChromeDriver: chrome on mac (e271...ff5b)] -> xpath: //*[@name='domain']]}");
    assertThat(new WebElementWrapper(driver, element, null)).hasToString("{xpath: //*[@name='domain']}");
  }

  @Test
  void toStringForWrappedSelenideElement() {
    when(element.toString()).thenReturn("Proxy element for: DefaultElementLocator 'By.xpath: //select[@name='wrong-select-name']'");

    assertThat(new WebElementWrapper(driver, element, null))
      .hasToString("{By.xpath: //select[@name='wrong-select-name']}");
  }
}
