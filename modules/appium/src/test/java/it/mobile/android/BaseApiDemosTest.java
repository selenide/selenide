package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.InteractsWithApps;
import it.mobile.ClickListener;
import it.mobile.ITTest;
import it.mobile.android.driverproviders.ci.AndroidDriverWithDemos;
import it.mobile.android.driverproviders.local.LocalAndroidDriverWithDemos;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.appium.SelenideAppium.launchApp;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static it.mobile.BrowserstackUtils.isCi;

public abstract class BaseApiDemosTest extends ITTest {
  private static final WebDriverListener listener = new ClickListener();

  @BeforeAll
  public static void setupMobileWebdriver() {
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.addListener(listener);
    Configuration.browser = isCi() ? AndroidDriverWithDemos.class.getName() : LocalAndroidDriverWithDemos.class.getName();
  }

  @AfterAll
  static void afterAll() {
    WebDriverRunner.removeListener(listener);
  }

  @BeforeEach
  final void runApplication() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      launchApp();
      return;
    }
    cast(WebDriverRunner.getWebDriver(), InteractsWithApps.class)
      .ifPresentOrElse(mobileDriver -> prepareApp(mobileDriver, AndroidDriverWithDemos.appId), SelenideAppium::launchApp);
  }
}

