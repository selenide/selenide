package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class FirefoxWithProfileTest {
  @Test
  public void createFirefoxWithCustomProfile() {
    if (!WebDriverRunner.FIREFOX.equalsIgnoreCase(Configuration.browser)) {
      return;
    }

    FirefoxProfile profile = new FirefoxProfile();
    try {
      profile.addExtension(new File(Thread.currentThread().getContextClassLoader().getResource("firebug-1.11.4.xpi").getPath()));
      profile.addExtension(new File(Thread.currentThread().getContextClassLoader().getResource("firepath-0.9.7-fx.xpi").getPath()));
      profile.setPreference("extensions.firebug.showFirstRunPage", false);
      profile.setPreference("extensions.firebug.allPagesActivation", "on");
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    profile.setEnableNativeEvents(true);
    profile.setPreference("intl.accept_languages", "no,en-us,en");
    profile.setPreference("extensions.firebug.console.enableSites", "true");
    WebDriver driver = new FirefoxDriver(profile);
    driver.manage().window().maximize();
    WebDriverRunner.setWebDriver(driver);

    open("http://google.com");
    $(By.name("q")).shouldBe(enabled).val("selenide").pressEnter();
  }
}
