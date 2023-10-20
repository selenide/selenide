package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.appium.SelenideAppium;
import it.mobile.ITTest;
import it.mobile.android.driverproviders.AndroidDriverWithDemos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseApiDemosTest extends ITTest {

  private static final EmptyListener listener = new EmptyListener();

  @BeforeAll
  public static void setupMobileWebdriver() {
    closeWebDriver();
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.addListener(listener);
    Configuration.browser = AndroidDriverWithDemos.class.getName();
  }

  @BeforeEach
  final void setUp() {
    WebDriverRunner.closeWebDriver();
    SelenideAppium.launchApp();
  }
}

