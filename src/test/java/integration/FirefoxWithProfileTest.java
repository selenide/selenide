package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SharedDownloadsFolder;
import integration.FirefoxProfileReader.FirefoxProfileChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.TestResources.toFile;
import static org.assertj.core.api.Assumptions.assumeThat;

final class FirefoxWithProfileTest extends BaseIntegrationTest {
  private SelenideDriver customFirefox;

  @BeforeEach
  void setUp() {
    assumeThat(browser().isFirefox()).isTrue();
  }

  @AfterEach
  void tearDown() {
    if (customFirefox != null) {
      customFirefox.close();
    }
  }

  @Test
  void createFirefoxWithCustomProfile() throws IOException {
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(createFirefoxProfileWithExtensions());
    if (browser().isHeadless()) options.addArguments("-headless");
    FirefoxDriver firefox = new FirefoxDriver(options);

    SelenideConfig config = new SelenideConfig().browser("firefox").baseUrl(getBaseUrl());
    customFirefox = new SelenideDriver(config, firefox, null, new SharedDownloadsFolder("build/downloads/456"));
    customFirefox.open("/page_with_selects_without_jquery.html");
    customFirefox.$("#non-clickable-element").shouldBe(visible);

    customFirefox.open("/page_with_jquery.html");
    customFirefox.$("#rememberMe").shouldBe(visible);

    FirefoxProfileChecker profile = new FirefoxProfileReader().readProfile(firefox);
    profile.assertPreference("plugin.state.flash", 42);
    profile.assertPreference("extensions.firebug.showFirstRunPage", false);
    profile.assertPreference("extensions.firebug.allPagesActivation", "on");
    profile.assertPreference("intl.accept_languages", "no,en-us,en");
  }

  private FirefoxProfile createFirefoxProfileWithExtensions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.addExtension(toFile("firebug-1.11.4.xpi"));
    profile.addExtension(toFile("firepath-0.9.7-fx.xpi"));
    profile.setPreference("extensions.firebug.showFirstRunPage", false);
    profile.setPreference("extensions.firebug.allPagesActivation", "on");
    profile.setPreference("intl.accept_languages", "no,en-us,en");
    profile.setPreference("extensions.firebug.console.enableSites", "true");
    profile.setPreference("plugin.state.flash", 42);
    return profile;
  }
}
