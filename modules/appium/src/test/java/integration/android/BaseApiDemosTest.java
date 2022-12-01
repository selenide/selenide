package integration.android;

import static com.codeborne.selenide.Selenide.closeWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.ITTest;
import integration.android.driverproviders.AndroidDriverWithDemos;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public abstract class BaseApiDemosTest extends ITTest {
  @BeforeAll
  public static void setup() {
    closeWebDriver();
    WebDriverRunner.addListener(new AbstractWebDriverEventListener() {
    });
    Configuration.browser = AndroidDriverWithDemos.class.getName();
  }
}

