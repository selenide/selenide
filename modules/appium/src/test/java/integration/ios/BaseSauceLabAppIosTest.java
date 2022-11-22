package integration.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.webdriver.HttpClientTimeouts;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static integration.Apps.downloadSauceLabIosApp;

class BaseSauceLabAppIosTest {

  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browser = IosDriverWithSwagLabs.class.getName();
    Configuration.browserSize = null;
    Configuration.remoteConnectionTimeout = Duration.ofMinutes(5).toMillis();
    Configuration.remoteReadTimeout = Duration.ofMinutes(5).toMillis();
    open();
  }
}

class IosDriverWithSwagLabs implements WebDriverProvider {
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    File app = downloadSauceLabIosApp();
    XCUITestOptions options = new XCUITestOptions();
    // on github actions first test run maybe extremely slow
    options.setWdaLaunchTimeout(Duration.ofMinutes(10));
    HttpClientTimeouts.defaultLocalReadTimeout = Duration.ofMinutes(10);
    options.setDeviceName("iPhone 12");
    options.setApp(app.getAbsolutePath());
    options.setFullReset(false);
    try {
      return new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
