package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverFactoryWithRemoteBrowser extends AbstractGridTest {
  @RegisterExtension
  static ScreenShooterExtension screenshotPerTest = new ScreenShooterExtension();
  private final ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

  @BeforeEach
  void setUp() {
    assumeThat(isChrome()).isTrue();
  }

  @Test
  void customWebdriverProviderCanUseRemoteWebdriver() {
    MyFactory.port = hubPort;
    Configuration.browser = MyFactory.class.getName();
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  void canTakeFullScreenshotRemote() throws IOException {
    MyFactory.port = hubPort;

    Configuration.browser = MyFactory.class.getName();

    openFile("page_with_selects_without_jquery.html");
    assertThat(screenshots.getContextScreenshots()).isEmpty();

    File screenshot = Selenide.screenshot(OutputType.FILE);
    BufferedImage img = ImageIO.read(screenshot);

    assertThat(img.getWidth()).isBetween(1000, 3000);
    assertThat(img.getHeight()).isBetween(1600, 3000);

    assertThat(screenshots.getContextScreenshots().get(0)).isSameAs(screenshot);
  }

  @ParametersAreNonnullByDefault
  static class MyFactory extends ChromeDriverFactory {
    static int port;

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
      ChromeOptions options = new ChromeOptions();
      options.setHeadless(config.headless());
      addSslErrorIgnoreCapabilities(options);

      RemoteWebDriver webDriver = new RemoteWebDriver(toURL("http://localhost:" + port + "/wd/hub"), options);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }

    @CheckReturnValue
    @Nonnull
    private static URL toURL(String url) {
      try {
        return new URL(url);
      }
      catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
