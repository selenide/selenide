package integration.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.junit5.TextReportExtension;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static integration.Apps.downloadSauceLabAndroidApp;

@ExtendWith(TextReportExtension.class)
public abstract class BaseSwagLabsAndroidTest {
  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browser = AndroidDriverWithSwagLabs.class.getName();
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
    SelenideAppium.launchApp();
  }
}

class AndroidDriverWithSwagLabs implements WebDriverProvider {
  @CheckReturnValue
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    File app = downloadSauceLabAndroidApp();

    UiAutomator2Options options = new UiAutomator2Options();
    options.merge(capabilities);
    options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
    options.setPlatformName("Android");
    options.setDeviceName("Android Emulator");
    options.setApp(app.getAbsolutePath());

    try {
      return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
