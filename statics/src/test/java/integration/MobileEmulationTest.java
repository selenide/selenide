package integration;

import com.codeborne.selenide.Device;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Device.GALAXY_S21;
import static com.codeborne.selenide.Device.IPHONE_14;
import static com.codeborne.selenide.Device.IPHONE_SE;
import static com.codeborne.selenide.Device.ONEPLUS_7T;
import static com.codeborne.selenide.Device.PIXEL_7;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.emulateDevice;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.Selenide.resetEmulation;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.logging.LogType.BROWSER;

final class MobileEmulationTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isSafari()).isFalse();
    assertThat(System.getProperty("chromeoptions.mobileEmulation")).isNull();
    assertThat(System.getProperty("edgeoptions.mobileEmulation")).isNull();
    timeout = 2000;
  }

  @AfterEach
  void tearDown() {
    System.clearProperty("chromeoptions.mobileEmulation");
    System.clearProperty("edgeoptions.mobileEmulation");
  }

  @AfterAll
  static void closeBrowser() {
    closeWebDriver();
  }

  @Test
  void canOpenBrowserInMobileEmulationMode() {
    assumeThat(isFirefox())
      .as("Firefox cannot enable mobile emulation via FirefoxOptions")
      .isFalse();

    closeWebDriver();
    System.setProperty("chromeoptions.mobileEmulation", "deviceName=Nexus 5");
    System.setProperty("edgeoptions.mobileEmulation", "deviceName=iPhone X");

    try {
      openFile("page_with_responsive_ui.html");
      $("#desktop").shouldBe(hidden);
      $("#mobile").shouldHave(text("Mobile"), visible);
    }
    finally {
      closeWebDriver();
    }
  }

  @Test
  void opensBrowserInDesktopModeByDefault() {
    openFile("page_with_responsive_ui.html");
    $("#desktop").shouldHave(text("Desktop"), visible);
    $("#mobile").shouldBe(hidden);
  }

  @ParameterizedTest
  @MethodSource("supportedDevices")
  void canSwitchToMobileModeOnTheFly(Device device) {
    openFile("page_with_responsive_ui.html");

    try {

      resetEmulation();
      $("#desktop").shouldHave(text("Desktop"), visible);
      $("#mobile").shouldBe(hidden);
      $("#width").shouldHave(text("Width: 1200").or(text("Width: 1192")));

      emulateDevice(device);

      $("#desktop").shouldBe(hidden);
      $("#mobile").shouldHave(text("Mobile"), visible);
      $("#width").shouldHave(text("Width: " + device.width()));
      $("#height").shouldHave(text("Height: " + device.height()));
      $("#ratio").shouldHave(text("Device Pixel Ratio: %.3f".formatted(device.pixelRatio())));

      resetEmulation();
      $("#mobile").shouldBe(hidden);
      $("#width").shouldHave(text("Width: 1200").or(text("Width: 1192")));
    }
    finally {
      List<String> webDriverLogs = getWebDriverLogs(BROWSER, Level.ALL);
      System.out.println(webDriverLogs);
    }
  }

  private static Stream<Arguments> supportedDevices() {
    return Stream.of(
      Arguments.of(IPHONE_SE),
      Arguments.of(IPHONE_14),
      Arguments.of(PIXEL_7),
      Arguments.of(ONEPLUS_7T),
      Arguments.of(GALAXY_S21)
    );
  }
}
