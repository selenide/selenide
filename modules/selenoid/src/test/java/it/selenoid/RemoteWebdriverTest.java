package it.selenoid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static it.selenoid.SelenoidSetup.capabilities;
import static it.selenoid.SelenoidSetup.checkDownload;
import static it.selenoid.SelenoidSetup.selenoidUrl;

@ExtendWith(SelenoidSetup.class)
public class RemoteWebdriverTest {
  @BeforeEach
  void setUp() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @Test
  void canStartRemoteWebDriver() throws IOException {
    RemoteWebDriver driver = new RemoteWebDriver(new URL(selenoidUrl()), capabilities(), false);
    setWebDriver(driver);
    checkDownload();
  }
}
