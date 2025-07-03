package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class MobileEmulationTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome()).isTrue();

    closeWebDriver();
    assertThat(System.getProperty("chromeoptions.mobileEmulation")).isNull();
  }

  @AfterEach
  void tearDown() {
    if (isChrome()) {
      closeWebDriver();
      System.clearProperty("chromeoptions.mobileEmulation");
    }
  }

  @Test
  void canOpenBrowserInMobileEmulationMode() {
    System.setProperty("chromeoptions.mobileEmulation", "deviceName=Nexus 5");

    openFile("page_with_responsive_ui.html");
    $("#desktop").shouldBe(hidden);
    $("#mobile").shouldHave(text("Mobile"), visible);
  }

  @Test
  void canOpenBrowserInDesktopMode() {
    System.clearProperty("chromeoptions.mobileEmulation");

    openFile("page_with_responsive_ui.html");
    $("#desktop").shouldHave(text("Desktop"), visible);
    $("#mobile").shouldBe(hidden);
  }
}
