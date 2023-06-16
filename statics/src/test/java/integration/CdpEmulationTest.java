package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;


public class CdpEmulationTest extends IntegrationTest {

  @Test
  public void emulateDimensionsTest() {
    Selenide.open("https://www.selenium.dev/documentation/webdriver/bidirectional/chrome_devtools");
    Selenide.cdp().createSession();
    Selenide.cdp().emulateMobileDevice(400, 600, 50);
  }

}
