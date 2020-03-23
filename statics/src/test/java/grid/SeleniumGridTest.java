package grid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

final class SeleniumGridTest extends AbstractGridTest {
  @BeforeEach
  void setUp() {
    Configuration.remote = "http://localhost:" + hubPort;
    Configuration.browser = "chrome";
    Configuration.headless = true;
    Configuration.proxyEnabled = true;
    System.setProperty("chromeoptions.mobileEmulation", "deviceName=Nexus 5");
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
    System.clearProperty("chromeoptions.mobileEmulation");
  }

  @Test
  void canUseSeleniumGrid() {
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  void shouldUseLocalFileDetector() {
    openFile("page_with_selects_without_jquery.html");
    RemoteWebDriver webDriver = (RemoteWebDriver) getWebDriver();

    assertThat(webDriver.getFileDetector()).isInstanceOf(LocalFileDetector.class);
  }
}
