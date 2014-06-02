package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSelenideElementTest {
  WebDriver webdriver;
  WebElement element;
  
  @Before
  public void mockWebDriver() {
    Configuration.timeout = 3;
    Configuration.pollingInterval = 1;
    Configuration.screenshots = false;
    
    Screenshots.screenshots = mock(ScreenShotLaboratory.class);
    when(Screenshots.screenshots.takeScreenShot()).thenReturn("");
    webdriver = mock(RemoteWebDriver.class);
    WebDriverRunner.setWebDriver(webdriver);

    element = mock(WebElement.class);
    when(element.getTagName()).thenReturn("h1");
    when(element.getText()).thenReturn("Hello world");
  }

  @Test
  public void elementShouldBeVisible() {
    when(element.isDisplayed()).thenReturn(true);
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    $("#firstName").shouldBe(visible);
  }

  @Test(expected = ElementNotFound.class)
  public void elementNotFound() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    $("#firstName").shouldBe(visible);
  }

  @Test(expected = ElementShould.class)
  public void elementFoundButNotMatched() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(false);
    $("#firstName").shouldBe(visible);
  }

  @Test(expected = ElementNotFound.class)
  public void elementFoundButInvisible() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenThrow(new WebDriverException("failed to call isDisplayed"));
    $("#firstName").shouldBe(visible);
  }

  @Test(expected = ElementShould.class)
  public void elementFoundButConditionCheckFailed() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(true);
    $("#firstName").shouldHave(text("goodbye"));
  }

  @Test
  public void elementNotFoundAsExpected() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    $("#firstName").shouldNotBe(exist);
    $("#firstName").shouldNotBe(present);
    $("#firstName").should(disappear);
    $("#firstName").shouldNotBe(visible);
    $("#firstName").shouldNotBe(enabled);
    $("#firstName").shouldNotHave(text("goodbye"));
  }
  
  @Test
  public void elementNotFoundAsExpected2() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenThrow(new WebDriverException("failed to call isDisplayed"));
    $("#firstName").shouldNot(exist);
    $("#firstName").shouldNotBe(present);
    $("#firstName").should(disappear);
    $("#firstName").shouldNotBe(visible);
    $("#firstName").shouldNotBe(enabled);
    $("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test(expected = InvalidSelectorException.class)
  public void webdriverReportsInvalidXpath_using_should() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    $("#firstName").should(disappear);
  }

  @Test(expected = InvalidSelectorException.class)
  public void webdriverReportsInvalidXpath_using_shouldNot() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    $("#firstName").shouldNot(exist);
  }
}