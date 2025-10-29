package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class RemoteWebDriverTest extends AbstractGridTest {
  @Nullable
  private WebDriver driver;

  @BeforeEach
  void setUp() {
    assumeThat(WebDriverRunner.isChrome()).isTrue();
    closeWebDriver();
  }

  @Test
  void canOpenCustomRemoteWebDriver() {
    driver = new Augmenter().augment(new RemoteWebDriver(gridUrl(), chromeOptions(null)));
    WebDriverRunner.setWebDriver(driver);
    openFile("page_with_uploads.html");

    File downloadedFile = $(byText("Download a PDF")).download(withExtension("pdf"));

    assertThat(downloadedFile.getName()).isEqualTo("minimal.pdf");
  }
}
