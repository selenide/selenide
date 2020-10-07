package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SharedDownloadsFolder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

import static com.codeborne.selenide.Condition.visible;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class FirefoxWithProfileTest extends BaseIntegrationTest {
  private SelenideDriver customFirefox;

  @BeforeEach
  void setUp() {
    assumeTrue(browser().isFirefox());
    WebDriverManager.firefoxdriver().setup();
  }

  @AfterEach
  void tearDown() {
    if (customFirefox != null) {
      customFirefox.close();
    }
  }

  @Test
  void createFirefoxWithCustomProfile() {
    FirefoxProfile profile = createFirefoxProfileWithExtensions();
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(profile);
    if (browser().isHeadless()) options.setHeadless(true);
    WebDriver firefox = new FirefoxDriver(options);

    SelenideConfig config = new SelenideConfig().browser("firefox").baseUrl(getBaseUrl());
    customFirefox = new SelenideDriver(config, firefox, null, new SharedDownloadsFolder("build/downloads/456"));
    customFirefox.open("/page_with_selects_without_jquery.html");
    customFirefox.$("#non-clickable-element").shouldBe(visible);

    customFirefox.open("/page_with_jquery.html");
    customFirefox.$("#rememberMe").shouldBe(visible);

  }

  private FirefoxProfile createFirefoxProfileWithExtensions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.addExtension(new File(currentThread().getContextClassLoader().getResource("firebug-1.11.4.xpi").getPath()));
    profile.addExtension(new File(currentThread().getContextClassLoader().getResource("firepath-0.9.7-fx.xpi").getPath()));
    profile.setPreference("extensions.firebug.showFirstRunPage", false);
    profile.setPreference("extensions.firebug.allPagesActivation", "on");
    profile.setPreference("intl.accept_languages", "no,en-us,en");
    profile.setPreference("extensions.firebug.console.enableSites", "true");
    return profile;
  }
}
