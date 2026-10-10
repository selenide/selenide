package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.FrameNotFoundError;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

final class SelenideDriverITest extends ITest {
  private SelenideDriver browser1;
  private SelenideDriver browser2;

  @BeforeEach
  void setUp() {
    driver().close();
    browser1 = new SelenideDriver(new SelenideConfig().browser(browser).headless(true).baseUrl(getBaseUrl()));
    browser2 = new SelenideDriver(new SelenideConfig().browser(browser).headless(true).baseUrl(getBaseUrl()));
  }

  @AfterEach
  void tearDown() {
    if (browser2 != null) browser2.close();
    if (browser1 != null) browser1.close();
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
  void canDownloadFilesInDifferentBrowsersViaDifferentProxies() {
    browser1.open("/page_with_uploads.html" + testName() + "&browser=" + browser1.config().browser());
    browser2.open("/page_with_uploads.html" + testName() + "&browser=" + browser2.config().browser());

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

  @Test
  void assertionScreenshotsUseEachBrowsersLaboratory() {
    InstanceScreenshots firstScreenshots = new InstanceScreenshots("first");
    InstanceScreenshots secondScreenshots = new InstanceScreenshots("second");
    browser1 = browser(firstScreenshots);
    browser2 = browser(secondScreenshots);
    browser1.open("/page_with_images.html");
    browser2.open("/page_with_images.html");

    assertThatThrownBy(() -> browser1.$("#missing").shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("first-");
    assertThat(firstScreenshots.screenshots()).hasSize(1);
    assertThat(secondScreenshots.screenshots()).isEmpty();

    assertThatThrownBy(() -> browser2.$$("#missing").shouldHave(size(1)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("second-");
    assertThat(firstScreenshots.screenshots()).hasSize(1);
    assertThat(secondScreenshots.screenshots()).hasSize(1);
  }

  @Test
  void elementScreenshotsUseTheBrowsersLaboratory() {
    InstanceScreenshots screenshots = spy(new InstanceScreenshots("element"));
    browser1 = browser(screenshots);
    browser1.open("/page_with_images.html");
    SelenideElement image = browser1.$("#valid-image img");

    File screenshot = image.screenshot();
    assertThat(screenshot).exists();
    assertThat(screenshot.getName()).startsWith("element-").endsWith(".png");
    assertThat(image.screenshotAsImage().getWidth()).isPositive();
    verify(screenshots).takeScreenshot(same(browser1.driver()), any(WebElement.class));
    verify(screenshots, times(2)).takeScreenshotAsImage(same(browser1.driver()), any(WebElement.class));
  }

  @Test
  void frameSwitchingFailuresUseTheBrowsersLaboratory() {
    InstanceScreenshots screenshots = new InstanceScreenshots("frame");
    browser1 = browser(screenshots);
    browser1.open("/page_with_images.html");

    assertThatThrownBy(() -> browser1.switchTo().frame("missing", Duration.ofMillis(1)))
      .isInstanceOf(FrameNotFoundError.class)
      .hasMessageContaining("frame-");
    assertThat(screenshots.screenshots()).hasSize(1);
  }

  private SelenideDriver browser(ScreenShotLaboratory screenshots) {
    SelenideConfig config = new SelenideConfig().browser(browser).headless(true).baseUrl(getBaseUrl())
      .browserCapabilities(defaultBrowserCapabilities()).timeout(50).pollingInterval(1);
    return new SelenideDriver(config, emptyList(), screenshots);
  }

  private static final class InstanceScreenshots extends ScreenShotLaboratory {
    private final String prefix;
    private final List<Screenshot> history = new CopyOnWriteArrayList<>();

    private InstanceScreenshots(String prefix) {
      this.prefix = prefix;
    }

    @Override
    protected String generateScreenshotFileName() {
      return prefix + "-" + screenshotCounter.incrementAndGet();
    }

    @Override
    protected void addToHistory(Screenshot screenshot) {
      history.add(screenshot);
    }

    @Override
    public List<Screenshot> screenshots() {
      return List.copyOf(history);
    }
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
