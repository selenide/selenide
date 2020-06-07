package grid;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;

public class CustomWebdriverProviderWithRemoteBrowser extends AbstractGridTest {
  @Test
  void customWebdriverProviderCanUseRemoteWebdriver() {
    MyProvider.port = hubPort;
    Configuration.browser = MyProvider.class.getName();
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @ParametersAreNonnullByDefault
  static class MyProvider implements WebDriverProvider {
    static int port;

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      ChromeOptions options = new ChromeOptions();
      options.setHeadless(true);
      addSslErrorIgnoreCapabilities(options);

      RemoteWebDriver webDriver = new RemoteWebDriver(toURL("http://localhost:" + port + "/wd/hub"), options);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }

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
