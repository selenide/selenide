package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class RemoteWebDriverTest extends AbstractGridTest {
  private RemoteWebDriver driver;

  @BeforeEach
  void setUp() {
    assumeThat(WebDriverRunner.isChrome()).isTrue();
    closeWebDriver();
  }

  @Test
  void canOpenCustomRemoteWebDriver() throws FileNotFoundException {
    driver = new RemoteWebDriver(gridUrl, chromeOptions(null));
    WebDriverRunner.setWebDriver(driver);
    openFile("page_with_uploads.html");

    File downloadedFile = $(byText("Download a PDF")).download(withExtension("pdf"));

    assertThat(downloadedFile.getName()).isEqualTo("minimal.pdf");
  }
}
