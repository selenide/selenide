package it.mobile.android.driverproviders.ci;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

import static it.mobile.BrowserstackUtils.browserstackUrl;
import static it.mobile.BrowserstackUtils.getBrowserstackOptions;

public abstract class AndroidDriverProvider implements WebDriverProvider {
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    UiAutomator2Options options = new UiAutomator2Options();
    options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
    options.setPlatformName("Android");
    options.setDeviceName("Google Pixel 8");
    options.setCapability("bstack:options", getBrowserstackOptions());
    options.setApp(getApplicationUnderTest());
    try {
      return new AndroidDriver(browserstackUrl(), options);
    } catch (SessionNotCreatedException e) {
      return new AndroidDriver(browserstackUrl(), options);
    }
  }

  protected abstract String getApplicationUnderTest();

}
