package it.mobile.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ClickListener;
import it.mobile.ITTest;
import it.mobile.ios.driverproviders.SwagLabsIosDriverProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.launchApp;

abstract class BaseSwagLabsAppIosTest extends ITTest {
  private static final WebDriverListener listener = new ClickListener();

  @BeforeEach
  public final void setUp() {
    closeWebDriver();
    Configuration.browser = SwagLabsIosDriverProvider.class.getName();
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

