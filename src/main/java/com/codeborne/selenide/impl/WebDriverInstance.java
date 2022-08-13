package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.drivercommands.CloseDriverCommand;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.github.bsideup.jabel.Desugar;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

/**
 * An instance of webdriver with its resources: config, proxy and downloads folder.
 * @since 6.7.2
 */
@ParametersAreNonnullByDefault
@Desugar
public record WebDriverInstance(
  Config config,
  WebDriver webDriver,
  @Nullable SelenideProxyServer proxy,
  @Nullable DownloadsFolder downloadsFolder) implements Disposable {

  private static final CloseDriverCommand closeDriverCommand = new CloseDriverCommand();

  public WebDriverInstance {
    requireNonNull(config, "config must not be null");
    requireNonNull(webDriver, "webDriver must not be null");
  }

  @Override
  public void dispose() {
    closeDriverCommand.close(this);
  }
}
