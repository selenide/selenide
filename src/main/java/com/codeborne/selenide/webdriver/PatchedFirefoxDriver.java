package com.codeborne.selenide.webdriver;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class PatchedFirefoxDriver extends FirefoxDriver {
  private static final Logger log = LoggerFactory.getLogger(PatchedFirefoxDriver.class);

  PatchedFirefoxDriver(GeckoDriverService driverService, FirefoxOptions capabilities) {
    super(driverService, capabilities);
  }

  @Override
  public void close() {
    try {
      super.close();
    }
    catch (ConnectionFailedException unableToEstablishWebsocketConnection) {
      log.error("Failed to disconnect DevTools session", unableToEstablishWebsocketConnection);
      execute(DriverCommand.CLOSE);
    }
  }
}
