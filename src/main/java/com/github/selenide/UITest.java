package com.github.selenide;

import com.github.selenide.jetty.Launcher;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public abstract class UITest {
  protected WebDriver browser;
  private Launcher server;

  @Before
  public void startServer() throws Exception {
    server = createLauncher();
    server.run();
  }

  protected Launcher createLauncher() {
    return new Launcher(8888);
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
    browser.get("http://localhost:" + server.getPort() + server.getDefaultWebapp() + servletName);
  }

  protected void openRelativeUrl(String relativeUrl) {
    browser.get("http://localhost:" + server.getPort() + relativeUrl);
  }
}
