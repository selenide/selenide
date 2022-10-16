package integration;

import integration.FirefoxProfileReader.FirefoxProfileChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assumptions.assumeThat;

final class FirefoxWithSystemPropertiesTest extends ITest {
  @BeforeEach
  void setUp() throws IOException {
    assumeThat(browser().isFirefox()).isTrue();
    driver().close();
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }

  @Test
  void createFirefoxWithCustomProfile() throws IOException {
    System.setProperty("firefoxprofile.intl.accept_languages", "no,en-us,en");
    System.setProperty("firefoxprofile.another.enabled", "true");
    System.setProperty("firefoxprofile.some.cap", "25");

    openFile("page_with_selects_without_jquery.html");
    $("#non-clickable-element").shouldBe(visible);

    FirefoxProfileChecker profile = new FirefoxProfileReader().readProfile((FirefoxDriver) driver().getWebDriver());
    profile.assertPreference("some.cap", 25);
    profile.assertPreference("another.enabled", true);
    profile.assertPreference("intl.accept_languages", "no,en-us,en");
  }
}
