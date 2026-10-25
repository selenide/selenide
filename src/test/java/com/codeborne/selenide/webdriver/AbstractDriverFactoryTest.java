package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.service.DriverService;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

final class AbstractDriverFactoryTest {
  private final AbstractDriverFactory factory = spy(AbstractDriverFactory.class);

  @Test
  void setsTimezoneForWebdriverProcess() {
    doReturn(false).when(factory).isWindows();
    DriverService.Builder<?, ?> builder = mock();
    factory.setTimezone(new SelenideConfig().timezone("Europe/Tallinn"), builder);
    verify(builder).withEnvironment(Map.of("TZ", "Europe/Tallinn"));
  }

  @Test
  void doesNotSetTimezoneIfNotConfigured() {
    DriverService.Builder<?, ?> builder = mock();
    factory.setTimezone(new SelenideConfig().timezone(null), builder);
    factory.setTimezone(new SelenideConfig().timezone(" "), builder);
    verifyNoInteractions(builder);
  }

  @Test
  void doesNotSetTimezoneOnWindows() {
    doReturn(true).when(factory).isWindows();
    DriverService.Builder<?, ?> builder = mock();
    factory.setTimezone(new SelenideConfig().timezone("Europe/Tallinn"), builder);
    verifyNoInteractions(builder);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Europe/Talinn", "GMT+02:00", "+02:00"})
  void failsOnInvalidTimezone(String timezone) {
    DriverService.Builder<?, ?> builder = mock();
    assertThatThrownBy(() -> factory.setTimezone(new SelenideConfig().timezone(timezone), builder))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid timezone: \"" + timezone + "\". Use a tz database ID like \"Europe/Tallinn\"");
    verifyNoInteractions(builder);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Asia/Tokyo", "UTC", "Etc/GMT-2"})
  void acceptsTzDatabaseIds(String timezone) {
    doReturn(false).when(factory).isWindows();
    DriverService.Builder<?, ?> builder = mock();
    factory.setTimezone(new SelenideConfig().timezone(timezone), builder);
    verify(builder).withEnvironment(Map.of("TZ", timezone));
  }

  @Test
  void majorVersion() {
    assertThat(factory.majorVersion(null)).isEqualTo(0);
    assertThat(factory.majorVersion("")).isEqualTo(0);
    assertThat(factory.majorVersion("1")).isEqualTo(1);
    assertThat(factory.majorVersion("2.3")).isEqualTo(2);
    assertThat(factory.majorVersion("6.17134")).isEqualTo(6);
    assertThat(factory.majorVersion("82.0.459.1")).isEqualTo(82);
  }

  @Test
  void convertStringToNearestObjectType() {
    assertThat(factory.convertStringToNearestObjectType("false")).as("Boolean").isEqualTo(false);
    assertThat(factory.convertStringToNearestObjectType("FALsE")).as("Boolean").isEqualTo(false);
    assertThat(factory.convertStringToNearestObjectType("tRUE")).as("Boolean").isEqualTo(true);
    assertThat(factory.convertStringToNearestObjectType("true")).as("Boolean").isEqualTo(true);

    assertThat(factory.convertStringToNearestObjectType("0")).as("Integer").isEqualTo(0);
    assertThat(factory.convertStringToNearestObjectType("-42")).as("Integer").isEqualTo(-42);

    assertThat(factory.convertStringToNearestObjectType("")).as("String").isEqualTo("");

    assertThat(factory.convertStringToNearestObjectType("Hottabych 2 false"))
      .as("any other value")
      .isEqualTo("Hottabych 2 false");
  }

  @SuppressWarnings("unchecked")
  @Test
  void merge() {
    ChromeOptions base = new ChromeOptions();
    base.addArguments("--start-maximized");
    base.setBinary("chrome.exe");
    base.setCapability("hello", "World");
    base.setCapability("goodbye", "World");

    ChromeOptions extra = new ChromeOptions();
    extra.addArguments("--start-incognito");
    extra.setBinary("chrome.dll");
    extra.setCapability("hello", "God");

    ChromeOptions options = factory.merge(base, extra);
    assertThat(options.getBrowserName()).isEqualTo("chrome");
    assertThat(options.getCapability("goodbye")).isEqualTo("World");
    assertThat(options.getCapability("hello")).as("extra options should override base options").isEqualTo("God");

    Map<String, Object> opts = (Map<String, Object>) options.getCapability("goog:chromeOptions");
    assertThat(opts).as("extra capabilities should override base capabilities").containsEntry("binary", "chrome.dll");
    assertThat(opts).as("base and extra arguments should be merged together")
      .containsEntry("args", asList("--start-maximized", "--start-incognito"));
  }

  @Test
  void cannotMergeDifferentBrowsers() {
    ChromeOptions base = new ChromeOptions();
    FirefoxOptions extra = new FirefoxOptions();

    assertThatThrownBy(() -> factory.merge(base, extra))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Conflicting browser name: 'chrome' vs. 'firefox'");
  }
}
