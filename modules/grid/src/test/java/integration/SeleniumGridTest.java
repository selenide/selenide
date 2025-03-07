package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

final class SeleniumGridTest extends AbstractGridTest {
  @BeforeEach
  void setUp() {
    Configuration.remote = gridUrl().toString();
    System.setProperty("chromeoptions.mobileEmulation", "deviceName=Nexus 5");
  }

  @AfterEach
  void tearDown() {
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
    RemoteWebDriver webDriver = unwrapRemoteWebDriver(getWebDriver());

    assertThat(webDriver.getFileDetector()).isInstanceOf(LocalFileDetector.class);
  }
}
