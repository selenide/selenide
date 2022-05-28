package com.codeborne.selenide.drivercommands;

import static com.codeborne.selenide.impl.Plugins.inject;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.CdpProvider;
import org.openqa.selenium.devtools.v102.storage.Storage;

public class DevTools {
  private final CdpProvider cdpProvider;
  private final Driver driver;

  public DevTools(Driver driver) {
    this.driver = driver;
    this.cdpProvider = inject(CdpProvider.class);
  }

  public org.openqa.selenium.devtools.DevTools seleniumDevTools() {
    return cdpProvider.getCdp(driver);
  }

  public void clearCookies() {
    try(var cdp = cdpProvider.getCdp(driver)) {
      cdp.send(Storage.clearDataForOrigin("*", "cookies"));
    }
  }

  public void clearLocalStorage() {
    try(var cdp = cdpProvider.getCdp(driver)) {
      cdp.send(Storage.clearDataForOrigin("*", "local_storage"));
    }
  }
}
