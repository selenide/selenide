package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

public class SelenideDriverFinalCleanupThread extends Thread {
  private final WebDriver driver;
  private final SelenideProxyServer proxy;
  private final CloseDriverCommand closeDriverCommand;

  SelenideDriverFinalCleanupThread(WebDriver driver, SelenideProxyServer proxy) {
    this(driver, proxy, new CloseDriverCommand());
  }

  SelenideDriverFinalCleanupThread(WebDriver driver, SelenideProxyServer proxy, CloseDriverCommand closeDriverCommand) {
    this.driver = driver;
    this.proxy = proxy;
    this.closeDriverCommand = closeDriverCommand;
  }

  @Override
  public void run() {
    closeDriverCommand.closeAsync(driver, proxy);
  }
}
