package com.codeborne.selenide.drivercommands;

import static com.codeborne.selenide.impl.Plugins.inject;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.CdpProvider;
import java.util.function.Consumer;
import org.openqa.selenium.devtools.v102.storage.Storage;

public class DevTools {
  private static final CdpProvider cdpProvider = inject(CdpProvider.class);
  private final Driver driver;

  public DevTools(Driver driver) {
    this.driver = driver;
  }

  public org.openqa.selenium.devtools.DevTools seleniumDevTools() {
    return cdpProvider.getCdp(driver);
  }

  public void runWithDevTools(Consumer<org.openqa.selenium.devtools.DevTools> block) {
    try (org.openqa.selenium.devtools.DevTools devTools = seleniumDevTools()) {
      block.accept(devTools);
    }
  }

  public void clearCookies() {
    runWithDevTools(cdp -> {
      cdp.send(Storage.clearDataForOrigin("*", "cookies"));
    });
  }

  public void clearLocalStorage() {
    runWithDevTools(cdp -> {
      cdp.send(Storage.clearDataForOrigin("*", "local_storage"));
    });
  }
}
