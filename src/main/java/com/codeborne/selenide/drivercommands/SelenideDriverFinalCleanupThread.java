package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

public class SelenideDriverFinalCleanupThread extends Thread {
  private final WebDriver driver;
  private final SelenideProxyServer proxy;

  SelenideDriverFinalCleanupThread(WebDriver driver, SelenideProxyServer proxy) {
    this.driver = driver;
    this.proxy = proxy;
  }

  @Override
  public void run() {
    new CloseDriverCommand(driver, proxy).run();
  }
}
