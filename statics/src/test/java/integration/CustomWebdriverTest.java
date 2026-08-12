package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.using;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
final class CustomWebdriverTest extends IntegrationTest {
  private WebDriver browser1;
  private WebDriver browser2;

  @Test
  void userCanSwitchBetweenWebdrivers_using_setWebDriver() {
    setWebDriver(browser1);
    openFile("page_with_selects_without_jquery.html");
    $("h1").shouldBe(visible).shouldHave(text("Page with selects"));

    setWebDriver(browser2);
    openFile("file_upload_form.html");
    $("h1").shouldBe(visible).shouldHave(text("File upload form"));

    setWebDriver(browser1);
    $("h1").shouldBe(visible).shouldHave(text("Page with selects"));
  }

  @Test
  void userCanSwitchBetweenWebdrivers_usingIn() {
    using(browser1, () -> {
      openFile("page_with_selects_without_jquery.html");
      $("h1").shouldBe(visible).shouldHave(text("Page with selects"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser1);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
    assertThat(browser1.getCurrentUrl()).contains("page_with_selects_without_jquery.html");

    using(browser2, () -> {
      openFile("file_upload_form.html");
      $("h1").shouldBe(visible).shouldHave(text("File upload form"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser2);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
    assertThat(browser2.getCurrentUrl()).contains("file_upload_form.html");

    using(browser1, () -> {
      $("h1").shouldBe(visible).shouldHave(text("Page with selects"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser1);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
    assertThat(browser1.getCurrentUrl()).contains("page_with_selects_without_jquery.html");

    using(browser2, () -> {
      $("h1").shouldBe(visible).shouldHave(text("File upload form"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser2);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
    assertThat(browser2.getCurrentUrl()).contains("file_upload_form.html");
  }

  @Test
  void userCanSwitchToCustomWebdriverAndBackToSelenideWebdriver() {
    openFile("page_with_big_divs.html");
    $("h1").shouldBe(visible).shouldHave(text("Some big divs"));

    using(browser1, () -> {
      openFile("page_with_selects_without_jquery.html");
      assertThat(WebDriverRunner.hasWebDriverStarted()).isTrue();
      $("h1").shouldBe(visible).shouldHave(text("Page with selects"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser1);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isTrue();
    $("h1").shouldBe(visible).shouldHave(text("Some big divs"));
  }

  @Test
  void using_canBeCalledAgain_afterPreviousUsingWithExternalDriver() {
    setWebDriver(browser1);
    openFile("page_with_selects_without_jquery.html");
    using(browser2, () -> openFile("file_upload_form.html"));
    using(browser1, () -> openFile("page_with_big_divs.html"));
  }

  @Test
  void canDownloadFilesAfterUsingAnotherBrowser() {
    openFile("page_with_uploads.html");
    using(browser2, () -> {
      openFile("page_with_selects_without_jquery.html");
    });

    File downloadedFile = $(byText("Download me")).download(file().withTimeout(ofSeconds(2)).withNameMatching("hello.*\\.txt"));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @BeforeAll
  void setUpTwoBrowsers() {
    assumeThat(isChrome() || isFirefox()).isTrue();
    closeWebDriver();
    browser1 = isFirefox() ? openFirefox() : openChrome();
    browser2 = isFirefox() ? openFirefox() : openChrome();
  }

  @BeforeEach
  void resetCurrentWebdriver() {
    WebDriverRunner.resetWebDriver();
    browser1.navigate().to("about:blank" + testName() + "&browser=first");
    browser2.navigate().to("about:blank" + testName() + "&browser=second");
  }

  @AfterAll
  void afterAll() {
    if (browser1 != null) browser1.quit();
    if (browser2 != null) browser2.quit();
    closeWebDriver();
  }
}
