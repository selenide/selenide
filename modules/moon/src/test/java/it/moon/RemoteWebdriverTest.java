package it.moon;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.TextReportExtension;
import integration.LogTestNameExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static it.moon.MoonSetup.capabilities;
import static it.moon.MoonSetup.checkDownload;
import static it.moon.MoonSetup.moonUrl;

@ExtendWith({MoonSetup.class, LogTestNameExtension.class, TextReportExtension.class})
public class RemoteWebdriverTest {
  @BeforeEach
  void setUp() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @Test
  void canStartRemoteWebDriver() throws IOException {
    RemoteWebDriver driver = new RemoteWebDriver(new URL(moonUrl()), capabilities(), false);
    setWebDriver(driver);
    checkDownload();
  }
}
