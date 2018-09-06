package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.assertj.core.api.Assumptions.assumeThat;

public class SelenideDriverITest extends IntegrationTest {
  private SelenideDriver browser1 = new SelenideDriver();
  private SelenideDriver browser2 = new SelenideDriver();

  @BeforeEach
  void setUp() {
    close();
  }

  @AfterEach
  void tearDown() {
    browser1.close();
    browser2.close();
  }

  @Test
  void canUseTwoBrowsersInSameThread() {
    browser1.open("/page_with_images.html?browser=" + Configuration.browser);
    browser2.open("/page_with_selects_without_jquery.html?browser=" + Configuration.browser);

    assertThat(hasWebDriverStarted()).isFalse();

    browser1.find("#valid-image img").shouldBe(visible);
    browser2.find("#password").shouldBe(visible);
    assertThat(browser1.title()).isEqualTo("Test::images");
    assertThat(browser2.title()).isEqualTo("Test page :: with selects, but withour JQuery");
  }

  @Test
  void canDownloadFilesInDifferentBrowsersViaDifferentProxies() throws FileNotFoundException {
    assumeThat(isPhantomjs()).isFalse();

    browser1.open("/page_with_uploads.html?browser=" + Configuration.browser);
    browser2.open("/page_with_uploads.html?browser=" + Configuration.browser);
    assertThat(hasWebDriverStarted()).isFalse();

    File file1 = browser1.$(byText("Download me")).download();
    File file2 = browser2.$(byText("Download file with cyrillic name")).download();

    assertThat(file1.getName()).isEqualTo("hello_world.txt");
    assertThat(file2.getName()).isEqualTo("файл-с-русским-названием.txt");
    assertThat(hasWebDriverStarted()).isFalse();
  }
}
