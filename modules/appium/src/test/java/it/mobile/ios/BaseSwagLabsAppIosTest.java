package it.mobile.ios;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.launchApp;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ITTest;
import it.mobile.ios.driverproviders.SwagLabsIosDriverProvider;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

abstract class BaseSwagLabsAppIosTest extends ITTest {
  @BeforeEach
  public final void setUp() {
    closeWebDriver();
    Configuration.browser = SwagLabsIosDriverProvider.class.getName();
    WebDriverRunner.addListener(new WebDriverListener() {
    });
    Configuration.timeout = 10_000;
    launchApp();
  }
}

