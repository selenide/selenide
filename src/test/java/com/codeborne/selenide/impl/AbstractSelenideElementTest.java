package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import com.codeborne.selenide.logevents.LogEventListener;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAILED;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASSED;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSelenideElementTest {
  RemoteWebDriver webdriver = mock(RemoteWebDriver.class);
  WebElement element = mock(WebElement.class);

  @Before
  public void mockWebDriver() {
    Configuration.timeout = 3;
    Configuration.pollingInterval = 1;
    Configuration.screenshots = false;

    WebDriverRunner.webdriverContainer = new WebDriverThreadLocalContainer();
    WebDriverRunner.setWebDriver(webdriver);
    when(webdriver
        .executeScript(anyString(), any(WebElement.class)))
        .thenReturn(ImmutableMap.of("id", "id1", "class", "class1"));

    Screenshots.screenshots = mock(ScreenShotLaboratory.class);
    when(Screenshots.screenshots.takeScreenShot()).thenReturn("");

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

  @Test(expected = ElementShould.class)
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
    when(webdriver.findElement(By.cssSelector("#firstName")))
        .thenThrow(new WebDriverException("element is not found and this is expected"));
    $("#firstName").shouldNot(exist);
    $("#firstName").shouldNotBe(present);
    $("#firstName").should(disappear);
    $("#firstName").shouldNotBe(visible);
    $("#firstName").shouldNotBe(enabled);
    $("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test(expected = InvalidSelectorException.class)
  public void webdriverReportsInvalidXpath_using_should() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
        .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    $("#firstName").should(disappear);
  }

  @Test(expected = InvalidSelectorException.class)
  public void webdriverReportsInvalidXpath_using_shouldNot() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
        .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    $("#firstName").shouldNot(exist);
  }

  @Test
  public void setValueShouldNotFailIfElementHasDisappearedWhileEnteringText() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(webdriver.executeScript(anyString(), anyVararg()))
        .thenThrow(new StaleElementReferenceException("element disappeared after entering text"));
    $("#firstName").setValue("john");
  }

  protected LogEventListener createListener(final String selector, final String subject, 
                                            final EventStatus status) {
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
  public void shouldLogSetValueSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "set value", PASSED));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    SelenideElement selEl = $("#firstName");
    selEl.setValue("ABC");
  }
  
  @Test
  public void shouldLogShouldSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should have", PASSED));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("ABC");
    SelenideElement selEl = $("#firstName");
    selEl.shouldHave(value("ABC"));
  }
  
  @Test
  public void shouldLogShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should not have", PASSED));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    SelenideElement selEl = $("#firstName");
    selEl.shouldNotHave(value("ABC"));
  }
  
  @Test(expected = ElementShould.class)
  public void shouldLogFailedShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener(createListener(selector, "should have", FAILED));
    
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    
    $("#firstName").shouldHave(value("ABC"));
  }
}