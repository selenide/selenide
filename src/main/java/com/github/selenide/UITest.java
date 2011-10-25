package com.github.selenide;

import com.github.selenide.jetty.Launcher;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertTrue;

public abstract class UITest {
  protected WebDriver browser;
  private Launcher server;

  @Before
  public void startServer() throws Exception {
    server = createLauncher();
    server.run();
  }

  protected Launcher createLauncher() {
    return new Launcher();
  }

  @Before
  public void openBrowser() {
    browser = new FirefoxDriver();
  }

  @After
  public void closeBrowser() {
    if (browser != null) {
      browser.close();
      browser.quit();
      browser = null;
    }
  }

  @After
  public void shutdownServer() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  protected void open(String servletName) {
    if (server.getDefaultWebapp() == null) {
      throw new IllegalStateException("No webapps deployed. Override method createLauncher() to create jetty launcher with your own web application.");
    }
    openRelativeUrl(server.getDefaultWebapp() + servletName);
  }

  protected void openRelativeUrl(String relativeUrl) {
    browser.get("http://localhost:" + server.getPort() + relativeUrl);
    assertTrue(browser.findElement(By.tagName("body")).isDisplayed());
  }
}
