package integration;

import java.io.File;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import static java.lang.Thread.currentThread;

class FirefoxWithProfileTest extends IntegrationTest {
  @Test
  void createFirefoxWithCustomProfile() {
    Assumptions.assumeTrue(WebDriverRunner.isFirefox());

    FirefoxProfile profile = createFirefoxProfileWithExtensions();
    WebDriver driver = new FirefoxDriver(new FirefoxOptions().setProfile(profile));
    driver.manage().window().maximize();
    try {
      WebDriverRunner.setWebDriver(driver);
      openFile("page_with_selects_without_jquery.html");

      WebDriverRunner.setWebDriver(driver);
      openFile("page_with_jquery.html");
    } finally {
      WebDriverRunner.closeWebDriver();
    }
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
