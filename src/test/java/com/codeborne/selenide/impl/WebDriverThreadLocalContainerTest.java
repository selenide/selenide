package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.close;
import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WebDriverThreadLocalContainerTest {
  WebDriverThreadLocalContainer container = spy(new WebDriverThreadLocalContainer());

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

  @Test
  public void checksIfBrowserIsStillAlive() {
    Configuration.reopenBrowserOnFail = true;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);
    
    assertSame(webdriver, container.getAndCheckWebDriver());
    verify(container).isBrowserStillOpen(any(WebDriver.class));
  }

  @Test
  public void doesNotReopenBrowserIfItFailed() {
    Configuration.reopenBrowserOnFail = false;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);
    
    assertSame(webdriver, container.getAndCheckWebDriver());
    verify(container, never()).isBrowserStillOpen(any(WebDriver.class));
  }

  @Test
  public void checksIfBrowserIsStillAlive_byCallingGetTitle() {
    WebDriver webdriver = mock(WebDriver.class);
    doReturn("blah").when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(true));
  }

  @Test
  public void isBrowserStillOpen_UnreachableBrowserException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(UnreachableBrowserException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }

  @Test
  public void isBrowserStillOpen_NoSuchWindowException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchWindowException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }

  @Test
  public void isBrowserStillOpen_NoSuchSessionException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchSessionException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }
}
