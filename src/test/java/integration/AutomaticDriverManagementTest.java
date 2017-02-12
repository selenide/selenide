package integration;

import com.codeborne.selenide.*;
import org.junit.Test;

/**
 * Created by sergey on 11.02.17.
 */
public class AutomaticDriverManagementTest extends IntegrationTest{

  @Test
  public void canStartChromeWithAutomaticDriver() throws Exception {
    Configuration.browser = WebDriverRunner.CHROME;
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }

  @Test
  public void canStartMarionetteWithAutomaticDriver() throws Exception {
    System.setProperty("webdriver.gecko.driver", "");
    Configuration.browser = WebDriverRunner.MARIONETTE;
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }
}
