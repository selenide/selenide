package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverProviderWithRemoteBrowser extends AbstractGridTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome()).isTrue();
  }

  @Test
  void customWebdriverProviderCanUseRemoteWebdriver() {
    MyProvider.url = gridUrl();
    Configuration.browser = MyProvider.class.getName();
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  static class MyProvider implements WebDriverProvider {
    @Nullable
    private static URL url;

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
      RemoteWebDriver webDriver = new RemoteWebDriver(requireNonNull(url), chromeOptions(null));
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }
  }
}
