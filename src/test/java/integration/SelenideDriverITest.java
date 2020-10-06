package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

final class SelenideDriverITest extends ITest {
  private SelenideDriver browser1;
  private SelenideDriver browser2;

  @BeforeEach
  void setUp() {
    driver().close();
    browser1 = new SelenideDriver(new SelenideConfig().browser(browser).baseUrl(getBaseUrl()));
    browser2 = new SelenideDriver(new SelenideConfig().browser(browser).baseUrl(getBaseUrl()));
  }

  @AfterEach
  void tearDown() {
    browser1.close();
    browser2.close();
  }

  @Test
  void canUseTwoBrowsersInSameThread() {
    browser1.open("/page_with_images.html?browser=" + browser1.config().browser());
    browser2.open("/page_with_selects_without_jquery.html?browser=" + browser2.config().browser());

    browser1.find("#valid-image img").shouldBe(visible);
    browser2.find("#password").shouldBe(visible);
    assertThat(browser1.title()).isEqualTo("Test::images");
    assertThat(browser2.title()).isEqualTo("Test page :: with selects, but without JQuery");
  }

  @Test
  void canDownloadFilesInDifferentBrowsersViaDifferentProxies() throws FileNotFoundException {
    browser1.open("/page_with_uploads.html?browser=" + browser1.config().browser());
    browser2.open("/page_with_uploads.html?browser=" + browser2.config().browser());

    File file1 = browser1.$(byText("Download me")).download();
    File file2 = browser2.$(byText("Download file with cyrillic name")).download();

    assertThat(file1.getName()).isEqualTo("hello_world.txt");
    assertThat(file2.getName()).isEqualTo("файл-с-русским-названием.txt");
  }

  @Test
  void canCreatePageObjects() {
    Page1 page1 = browser1.open("/page_with_images.html?browser=" + browser1.config().browser(), Page1.class);
    Page2 page2 = browser2.open("/page_with_selects_without_jquery.html?browser=" + browser2.config().browser(), Page2.class);

    page1.img.shouldBe(visible);
    page2.password.shouldBe(visible);
  }

  private static class Page1 {
    @FindBy(css = "#valid-image img")
    SelenideElement img;
  }

  private static class Page2 {
    @FindBy(id = "password")
    SelenideElement password;
  }
}
