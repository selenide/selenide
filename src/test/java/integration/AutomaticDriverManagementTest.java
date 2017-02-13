package integration;

import com.codeborne.selenide.*;
import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isMarionette;
import static org.junit.Assume.assumeTrue;

/**
 * Created by sergey on 11.02.17.
 */
public class AutomaticDriverManagementTest extends IntegrationTest {

  @Test
  public void canStartChromeWithAutomaticDriver() throws Exception {
    assumeTrue(isChrome());
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }

  @Test
  public void canStartMarionetteWithAutomaticDriver() throws Exception {
    assumeTrue(isMarionette());
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }
}
