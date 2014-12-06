package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.impl.SelenideLogger.EventStatus;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
    when(element.isDisplayed()).thenReturn(true);
  }
  
  @After
  public void after() {
    SelenideLogger.clearListeners();
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
  
  protected LogEventListener createListener(final String selector, final String subject, 
                                            final String status) {
    return new LogEventListener() {
      @Override
      public void onEvent(LogEvent currentLog) {
        System.out.println( "{" + currentLog.getElement() + "} " +
                currentLog.getSubject() + ": " + currentLog.getStatus()
            );
        assertThat(currentLog.getElement(), containsString(selector));
        assertThat(currentLog.getSubject(), containsString(subject));
        assertEquals(currentLog.getStatus(), status);
      }
    };
  }
  
  @Test
  public void shouldLogSetValueSubject() throws Exception {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "set value", EventStatus.PASSED.name()));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    SelenideElement selEl = $("#firstName");
    selEl.setValue("ABC");
  }
  
  @Test
  public void shouldLogShouldSubject() throws Exception {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should have", EventStatus.PASSED.name()));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("ABC");
    SelenideElement selEl = $("#firstName");
    selEl.shouldHave(value("ABC"));
  }
  
  @Test
  public void shouldLogShouldNotSubject() throws Exception {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should not have", EventStatus.PASSED.name()));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    SelenideElement selEl = $("#firstName");
    selEl.shouldNotHave(value("ABC"));
  }
  
  @Test(expected = ElementShould.class)
  public void shouldLogFailedShouldNotSubject() throws Exception {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should have", EventStatus.FAILED.name()));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    
    $("#firstName").shouldHave(value("ABC"));
  }

}