package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.close;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WebDriverThreadLocalContainerTest {
  WebDriverThreadLocalContainer container = new WebDriverThreadLocalContainer();

  @Before
  public void setUp() {
    container.factory = mock(WebDriverFactory.class);
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(any(Proxy.class));
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(null);
    WebDriverRunner.setProxy(null);
  }

  @After
  public void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
  }

  @Test
  public void createWebDriverWithoutProxy() {
    Configuration.fileDownload = HTTPGET;
    
    container.createDriver();
    
    verify(container.factory).createWebDriver(null);
  }

  @Test
  public void createWebDriverWithSelenideProxyServer() {
    Configuration.fileDownload = PROXY;
    
    container.createDriver();

    ArgumentCaptor<Proxy> captor = ArgumentCaptor.forClass(Proxy.class);
    verify(container.factory).createWebDriver(captor.capture());
    assertThat(captor.getValue().getHttpProxy(), is(notNullValue()));
    assertThat(captor.getValue().getSslProxy(), is(notNullValue()));
  }
}
