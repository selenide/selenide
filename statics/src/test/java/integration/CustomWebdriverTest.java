package integration;

import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.using;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverTest extends IntegrationTest {
  private WebDriver browser1;
  private WebDriver browser2;

  @BeforeAll
  static void setUpWebdrivers() {
    assumeThat(isChrome() || isFirefox()).isTrue();

    closeWebDriver();
    if (isFirefox()) WebDriverManager.firefoxdriver().setup();
    if (isChrome()) WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  void setUpTwoBrowsers() {
    closeWebDriver();

    browser1 = isFirefox() ?
      new FirefoxDriver(addSslErrorIgnoreCapabilities(addHeadless(new FirefoxOptions()))) :
      new ChromeDriver(addSslErrorIgnoreCapabilities(addHeadless(new ChromeOptions())));
    browser2 = isFirefox() ?
      new FirefoxDriver(addSslErrorIgnoreCapabilities(addHeadless(new FirefoxOptions()))) :
      new ChromeDriver(addSslErrorIgnoreCapabilities(addHeadless(new ChromeOptions())));
  }

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

    using(browser2, () -> {
      openFile("file_upload_form.html");
      $("h1").shouldBe(visible).shouldHave(text("File upload form"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser2);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();

    using(browser1, () -> {
      $("h1").shouldBe(visible).shouldHave(text("Page with selects"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser1);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();

    using(browser2, () -> {
      $("h1").shouldBe(visible).shouldHave(text("File upload form"));
      assertThat(WebDriverRunner.getWebDriver()).isSameAs(browser2);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
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
  void canDownloadFilesAfterUsing() throws IOException {
    openFile("page_with_uploads.html");
    using(browser2, () -> {
      openFile("page_with_selects_without_jquery.html");
    });

    File downloadedFile = $(byText("Download me")).download(DownloadOptions.using(FOLDER).withFilter(withExtension("txt")));

    assertThat(downloadedFile.getName()).matches("hello_world.*\\.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @AfterEach
  void tearDown() {
    if (browser1 != null) browser1.quit();
    if (browser2 != null) browser2.quit();
    closeWebDriver();
  }
}
