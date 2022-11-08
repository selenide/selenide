package integration.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static org.openqa.selenium.remote.CapabilityType.APPLICATION_NAME;

public class BaseAndroidTest {
  @BeforeEach
  public void setUp() {
    closeWebDriver();
    Configuration.browserSize = null;
    Configuration.browser = AndroidDriverProvider.class.getName();
//    TODO support these listeners as well
//    WebDriverRunner.addListener(new WebDriverListener() {
//      @Override
//      public void beforeAnyCall(Object target, Method method, Object[] args) {
//      }
//    });
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
    open();
  }

  @AfterEach
  public void tearDown() {
    closeWebDriver();
  }
}

class AndroidDriverProvider implements WebDriverProvider {
  @CheckReturnValue
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    UiAutomator2Options options = new UiAutomator2Options();
    options.merge(capabilities);
    options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
    options.setPlatformName("Android");
    options.setDeviceName("Android Emulator");
    options.setCapability(APPLICATION_NAME, "Appium");
    options.setAppPackage("com.android.calculator2");
    options.setAppActivity("com.android.calculator2.Calculator");
    options.setNewCommandTimeout(Duration.ofSeconds(11));
    options.setFullReset(false);
    try {
      return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
