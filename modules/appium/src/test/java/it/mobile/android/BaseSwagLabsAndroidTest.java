package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ITTest;
import it.mobile.android.driverproviders.AndroidDriverWithSwagLabs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseSwagLabsAndroidTest extends ITTest {

  private static final WebDriverListener listener = new ClickListener();

  @BeforeAll
  public static void setup() {
    closeWebDriver();
    Configuration.browser = AndroidDriverWithSwagLabs.class.getName();
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.addListener(listener);
  }

  @AfterAll
  static void afterAll() {
    WebDriverRunner.removeListener(listener);
  }

  @BeforeEach
  final void closePreviousDriver() {
    WebDriverRunner.closeWebDriver();
  }
}

