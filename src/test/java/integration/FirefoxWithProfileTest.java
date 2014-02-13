package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;

public class FirefoxWithProfileTest {
  @Test
  public void createFirefoxWithCustomProfile() {
    if (!WebDriverRunner.isFirefox()) {
      return;
    }

    FirefoxProfile profile = createFirefoxProfileWithExtensions();
    WebDriver driver = new FirefoxDriver(profile);
    driver.manage().window().maximize();
    try {
      WebDriverRunner.setWebDriver(driver);
      open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));

      WebDriverRunner.setWebDriver(driver);
      open(currentThread().getContextClassLoader().getResource("page_with_jquery.html"));
    }
    finally {
      WebDriverRunner.closeWebDriver();
    }
  }

  private FirefoxProfile createFirefoxProfileWithExtensions() {
    FirefoxProfile profile = new FirefoxProfile();
    try {
      profile.addExtension(new File(currentThread().getContextClassLoader().getResource("firebug-1.11.4.xpi").getPath()));
      profile.addExtension(new File(currentThread().getContextClassLoader().getResource("firepath-0.9.7-fx.xpi").getPath()));
      profile.setPreference("extensions.firebug.showFirstRunPage", false);
      profile.setPreference("extensions.firebug.allPagesActivation", "on");
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    profile.setEnableNativeEvents(true);
    profile.setPreference("intl.accept_languages", "no,en-us,en");
    profile.setPreference("extensions.firebug.console.enableSites", "true");
    return profile;
  }
}
