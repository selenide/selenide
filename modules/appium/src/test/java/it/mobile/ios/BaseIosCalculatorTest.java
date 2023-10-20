package it.mobile.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ITTest;
import it.mobile.ios.driverproviders.CalculatorDriverProvider;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.launchApp;

public abstract class BaseIosCalculatorTest extends ITTest {
  @BeforeEach
  final void setUp() {
    closeWebDriver();
    Configuration.browser = CalculatorDriverProvider.class.getName();
    WebDriverRunner.addListener(new WebDriverListener() {
    });
    launchApp();
  }
}

