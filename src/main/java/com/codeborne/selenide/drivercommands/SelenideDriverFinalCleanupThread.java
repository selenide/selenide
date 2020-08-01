package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class SelenideDriverFinalCleanupThread implements Runnable {
  private final Config config;
  private final WebDriver driver;
  private final SelenideProxyServer proxy;
  private final File browserDownloadsFolder;
  private final CloseDriverCommand closeDriverCommand;

  SelenideDriverFinalCleanupThread(Config config, WebDriver driver, @Nullable SelenideProxyServer proxy, File browserDownloadsFolder) {
    this(config, driver, proxy, browserDownloadsFolder, new CloseDriverCommand());
  }

  SelenideDriverFinalCleanupThread(Config config, WebDriver driver, @Nullable SelenideProxyServer proxy, File browserDownloadsFolder,
                                   CloseDriverCommand closeDriverCommand) {
    this.config = config;
    this.driver = driver;
    this.proxy = proxy;
    this.browserDownloadsFolder = browserDownloadsFolder;
    this.closeDriverCommand = closeDriverCommand;
  }

  @Override
  public void run() {
    closeDriverCommand.closeAsync(config, driver, proxy, browserDownloadsFolder);
  }
}
