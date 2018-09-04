package integration;

import com.codeborne.selenide.WebDriverRunner;
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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FirefoxWithProfileTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeTrue(isFirefox());
    close();
    toggleProxy(false);
    WebDriverManager.firefoxdriver().setup();
  }

  @AfterEach
  void tearDown() {
    close();
  }

  @Test
  void createFirefoxWithCustomProfile() {
    FirefoxProfile profile = createFirefoxProfileWithExtensions();
    WebDriver driver = new FirefoxDriver(new FirefoxOptions().setProfile(profile));
    driver.manage().window().maximize();

    WebDriverRunner.setWebDriver(driver);
    openFile("page_with_selects_without_jquery.html");
    $("#non-clickable-element").shouldBe(visible);

    openFile("page_with_jquery.html");
    $("#rememberMe").shouldBe(visible);
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
