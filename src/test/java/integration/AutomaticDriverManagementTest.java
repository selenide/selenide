package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.junit.Assume.assumeTrue;

/**
 * Created by sergey on 11.02.17.
 */
public class AutomaticDriverManagementTest extends IntegrationTest {

  private static final String oldProp = System.getProperty("webdriver.chrome.driver");

  @Before
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", "");
  }


  @Test
  public void canStartChromeWithAutomaticDriver() throws Exception {
    assumeTrue(isChrome());
    Selenide.open("/start_page.html");
    Selenide.$("#start-selenide").shouldHave(Condition.text("Start page"));
  }

  @After
  public void tearDown() {
    System.setProperty("webdriver.chrome.driver", oldProp);
  }
}
