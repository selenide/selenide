package it.mobile.android.driverproviders;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
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
      AndroidDriver androidDriver = new AndroidDriver(url(), options);
      return addOldSchoolListener(addListener(androidDriver));
    } catch (SessionNotCreatedException e) {
      AndroidDriver androidDriver = new AndroidDriver(url(), options);
      return addOldSchoolListener(addListener(androidDriver));
    }
  }

  @Nonnull
  @CheckReturnValue
  @SuppressWarnings("deprecation")
  private WebDriver addOldSchoolListener(WebDriver webDriver) {
    EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(webDriver);
    eventFiringWebDriver.register(new OldSchoolListener());
    return eventFiringWebDriver;
  }

  private WebDriver addListener(WebDriver webDriver) {
    WebDriverListener listener = new ClickListener();
    return new EventFiringDecorator<>(listener).decorate(webDriver);
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
      return new URL("http://127.0.0.1:4723/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("deprecation")
  private static class OldSchoolListener extends AbstractWebDriverEventListener {
    private static final Logger log = LoggerFactory.getLogger(OldSchoolListener.class);

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
      log.info("before click {}", element);
    }
  }

  public static class ClickListener implements WebDriverListener {
    private static final Logger log = LoggerFactory.getLogger(ClickListener.class);

    @Override
    public void beforeClick(WebElement element) {
      log.info("before click {}", element);
    }
  }
}
