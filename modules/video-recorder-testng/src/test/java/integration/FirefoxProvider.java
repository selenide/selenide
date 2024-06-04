package integration;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.Nonnull;

/**
 * Created by Serhii Bryt
 * 27.05.2024 12:14
 **/
public class FirefoxProvider implements WebDriverProvider {
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    FirefoxOptions chromeOptions = new FirefoxOptions();
    chromeOptions.setCapability("webSocketUrl", true);
    chromeOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
    return new FirefoxDriver(chromeOptions);
  }
}
