package it.mobile.android.driverproviders;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AndroidDriverProvider implements WebDriverProvider {
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    UiAutomator2Options options = getUiAutomator2Options();
    options.setApp(getApplicationUnderTest().getAbsolutePath());
    try {
      return new AndroidDriver(url(), options);
    } catch (SessionNotCreatedException e) {
      return new AndroidDriver(url(), options);
    }
  }

  protected abstract File getApplicationUnderTest();

  protected UiAutomator2Options getUiAutomator2Options() {
    UiAutomator2Options options = new UiAutomator2Options();
    options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
    options.setPlatformName("Android");
    options.setDeviceName("Android Emulator");
    return options;
  }

  private static URL url() {
    try {
      return new URL("http://127.0.0.1:4723/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

}
