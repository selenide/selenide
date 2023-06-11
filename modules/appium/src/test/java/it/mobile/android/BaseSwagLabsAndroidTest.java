package it.mobile.android;

import static com.codeborne.selenide.Selenide.closeWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import it.mobile.ITTest;
import it.mobile.android.driverproviders.AndroidDriverWithSwagLabs;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public abstract class BaseSwagLabsAndroidTest extends ITTest {
  @BeforeAll
  public static void setup() {
    closeWebDriver();
    Configuration.browser = AndroidDriverWithSwagLabs.class.getName();
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
  }
}

