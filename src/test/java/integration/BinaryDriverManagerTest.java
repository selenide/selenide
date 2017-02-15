package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.junit.Assume.assumeTrue;

/**
 * Created by sepi on 15.02.17.
 */
public class BinaryDriverManagerTest extends IntegrationTest {

  @Test
  public void canStartChromeWithAutomaticDriver() throws Exception {
    assumeTrue(isChrome());
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }

}
