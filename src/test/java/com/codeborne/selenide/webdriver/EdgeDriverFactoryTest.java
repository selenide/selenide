package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class EdgeDriverFactoryTest {
  private final Browser browser = new Browser("edge", false);
  private final EdgeDriverFactory factory = new EdgeDriverFactory();

  @Test
  void headless() {
    SelenideConfig config = new SelenideConfig().headless(true);

    EdgeOptions edgeOptions = factory.createCapabilities(config, browser, null, new File("/tmp/downloads-folder-12345"));
    Map<String, Object> options = edgeOptions(edgeOptions);

    List<String> args = args(options);
    assertThat(args).contains(
      "--headless=new",
      "--proxy-bypass-list=<-loopback>",
      "--disable-background-networking",
      "--disable-sync",
      "--window-size=1366,768"
    );
    assertThat(options.get("extensions")).isEqualTo(emptyList());

    Map<String, Object> prefs = prefs(options);
    assertThat(prefs).hasSize(5);
    assertThat(prefs.get("credentials_enable_service")).isEqualTo(false);
    assertThat(prefs.get("plugins.always_open_pdf_externally")).isEqualTo(true);
    assertThat(prefs.get("profile.default_content_setting_values.automatic_downloads")).isEqualTo(1);
    assertThat(prefs.get("safebrowsing.enabled")).isEqualTo(true);
    assertThat((String) prefs.get("download.default_directory")).endsWith("downloads-folder-12345");
  }

  @Test
  void non_headless() {
    SelenideConfig config = new SelenideConfig().headless(false);

    EdgeOptions edgeOptions = factory.createCapabilities(config, browser, null, new File("/tmp/downloads-folder-456789"));

    Map<String, Object> options = edgeOptions(edgeOptions);
    assertThat(args(options)).containsExactly(
      "--remote-allow-origins=*",
      "--proxy-bypass-list=<-loopback>",
      "--disable-dev-shm-usage",
      "--window-size=1366,768"
    );

    Map<String, Object> prefs = prefs(options);
    assertThat(prefs).hasSize(5);
    assertThat(prefs.get("credentials_enable_service")).isEqualTo(false);
    assertThat(prefs.get("plugins.always_open_pdf_externally")).isEqualTo(true);
    assertThat(prefs.get("profile.default_content_setting_values.automatic_downloads")).isEqualTo(1);
    assertThat(prefs.get("safebrowsing.enabled")).isEqualTo(true);
    assertThat((String) prefs.get("download.default_directory")).endsWith("downloads-folder-456789");
  }

  @Test
  void mergesDefaultCapabilities() {
    EdgeOptions options = factory.createCapabilities(new SelenideConfig(), browser, null, new File("/tmp/downloads-folder-456789"));

    assertThat(options.getCapability("acceptInsecureCerts")).isEqualTo(TRUE);
    assertThat(options.getCapability("browserName")).isEqualTo("MicrosoftEdge");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> edgeOptions(EdgeOptions edgeOptions) {
    return (Map<String, Object>) edgeOptions.asMap().get("ms:edgeOptions");
  }

  @SuppressWarnings("unchecked")
  private List<String> args(Map<String, Object> options) {
    return (List<String>) options.get("args");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> prefs(Map<String, Object> options) {
    return (Map<String, Object>) options.get("prefs");
  }
}
