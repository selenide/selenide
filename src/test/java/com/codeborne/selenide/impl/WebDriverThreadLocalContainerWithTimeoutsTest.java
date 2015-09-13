package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class WebDriverThreadLocalContainerWithTimeoutsTest {
  long currentThreadId = Thread.currentThread().getId();
  List<Thread> shutdownHooks = new ArrayList<>();

  WebDriverThreadLocalContainerWithTimeouts container = spy(new WebDriverThreadLocalContainerWithTimeouts() {
    @Override
    protected void addShutdownHook(Thread hook) {
      super.addShutdownHook(hook);
      shutdownHooks.add(hook);
    }
  });

  @Before
  public void setUp() {
    Configuration.browser = FIREFOX;
    Configuration.startMaximized = false;
    doReturn(mock(WebDriver.class)).when(container).createFirefoxDriver();
  }

  @Test
  public void noWebdriversCreatedByDefault() {
    assertThat(container.CREATED_WEB_DRIVERS.size(), equalTo(0));
    assertThat(container.THREAD_WEB_DRIVERS.size(), equalTo(0));
    assertThat(container.THREAD_WEB_DRIVER.size(), equalTo(0));
  }

  @Test
  public void shutdownProcessIsNotStartedByDefault() {
    assertThat(container.systemShutdownStarted.get(), is(false));
  }

  @Test
  public void cleanupProcessIsNotStartedByDefault() {
    assertThat(container.cleanupThreadStarted.get(), is(false));
  }

  @Test
  public void userCanSetWebdriverManually() throws InterruptedException {
    WebDriver webdriver = mock(WebDriver.class);
    container.setWebDriver(webdriver);

    assertThat(container.CREATED_WEB_DRIVERS.size(), equalTo(0));
    assertThat(container.THREAD_WEB_DRIVERS.size(), equalTo(0));
    assertThat(container.THREAD_WEB_DRIVER.size(), equalTo(1));
    assertSame(webdriver, container.THREAD_WEB_DRIVER.get(currentThreadId));
    assertSame(webdriver, container.getWebDriver());

    assertThat(container.cleanupThreadStarted.get(), is(false));

    // should not kill webdrivers that we didn't create
    container.unusedWebdriversCleanupThread.start();
    Thread.sleep(1000);
    verifyBrowserNotClosed(webdriver);
    
    runAllShutdownHooks();
    verifyBrowserNotClosed(webdriver);
  }

  @Test
  public void webdriverShouldBeCreatedAutomaticallyOnDemandAndBoundToCurrentThread() throws InterruptedException {
    WebDriver webdriver = container.getWebDriver();

    assertThat(container.THREAD_WEB_DRIVERS.size(), equalTo(1));
    assertThat(container.THREAD_WEB_DRIVER.size(), equalTo(1));
    assertSame(webdriver, container.THREAD_WEB_DRIVER.get(currentThreadId));

    Thread.sleep(210);
    assertThat(container.CREATED_WEB_DRIVERS.size(), equalTo(1));

    // starts cleanup thread when creates the first webdriver
    assertThat(container.cleanupThreadStarted.get(), is(true));

    // should not kill webdrivers because current thread is still active
    Thread.sleep(1000);
    assertThat(container.THREAD_WEB_DRIVERS.size(), equalTo(1));
    verifyBrowserNotClosed(webdriver);

    runAllShutdownHooks();
    verify(webdriver).quit();
  }

  private void verifyBrowserNotClosed(WebDriver webdriver) {
    verify(webdriver, never()).close();
    verify(webdriver, never()).quit();
  }
  
  private void runAllShutdownHooks() throws InterruptedException {
    for (Thread shutdownHook : shutdownHooks) {
      shutdownHook.start();
    }
    Thread.sleep(200);
    for (Thread shutdownHook : shutdownHooks) {
      shutdownHook.join();
    }
  }
}
