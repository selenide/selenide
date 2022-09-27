package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

public class RemoteWebDriverTest extends AbstractGridTest {
  private RemoteWebDriver driver;

  @BeforeEach
  void setUp() {
    assumeThat(WebDriverRunner.isChrome()).isTrue();
    closeWebDriver();
  }

  @Test
  void canOpenCustomRemoteWebDriver() throws FileNotFoundException {
    driver = new RemoteWebDriver(gridUrl, getChromeOptions());
    WebDriverRunner.setWebDriver(driver);
    openFile("page_with_uploads.html");

    File downloadedFile = $(byText("Download a PDF")).download(withExtension("pdf"));

    assertThat(downloadedFile.getName()).isEqualTo("minimal.pdf");
  }

  @Nonnull
  private static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
    options.setHeadless(Configuration.headless);
    return options;
  }
}
