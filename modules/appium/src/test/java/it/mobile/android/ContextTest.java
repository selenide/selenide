package it.mobile.android;

import com.codeborne.selenide.Stopwatch;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.getContextHandles;
import static com.codeborne.selenide.appium.SelenideAppium.getCurrentContext;
import static com.codeborne.selenide.appium.SelenideAppium.openAndroidDeepLink;
import static com.codeborne.selenide.appium.SelenideAppium.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

class ContextTest extends BaseSwagLabsAndroidTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void contexts() {
    openAndroidDeepLink("mydemoapprn://webview", "com.saucelabs.mydemoapp.rn");
    $(AppiumBy.accessibilityId("URL input field")).shouldBe(visible).setValue("www.google.com");
    $(AppiumBy.accessibilityId("Go To Site button")).shouldBe(visible).click();
    Stopwatch.sleepAtLeast(4000);
    switchTo().context("WEBVIEW_com.saucelabs.mydemoapp.rn");

    assertThat(getContextHandles())
      .hasSize(2)
      .contains("WEBVIEW_com.saucelabs.mydemoapp.rn")
      .contains("NATIVE_APP");
    assertThat(getCurrentContext())
      .isEqualTo("WEBVIEW_com.saucelabs.mydemoapp.rn");

    $(AppiumBy.name("q")).type("I can type in mobile as well");
  }
}
