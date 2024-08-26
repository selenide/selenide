package it.mobile.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ClickListener;
import it.mobile.ITTest;
import it.mobile.ios.driverproviders.IosDriverProvider;
import it.mobile.ios.driverproviders.LocalIosDriverProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.launchApp;
import static it.mobile.BrowserstackUtils.isCi;

abstract class BaseSwagLabsAppIosTest extends ITTest {
  private static final WebDriverListener listener = new ClickListener();

  @BeforeEach
  public final void setUp() {
    closeWebDriver();
    Configuration.browser = isCi() ? IosDriverProvider.class.getName() : LocalIosDriverProvider.class.getName();
    launchApp();
  }

  @BeforeAll
  static void beforeAll() {
    WebDriverRunner.addListener(listener);
  }

  @AfterAll
  static void afterAll() {
    WebDriverRunner.removeListener(listener);
  }
}

