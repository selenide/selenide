package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverProviderWithRemoteBrowser extends AbstractGridTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome()).isTrue();
  }

  @Test
  void customWebdriverProviderCanUseRemoteWebdriver() {
    MyProvider.url = gridUrl;
    Configuration.browser = MyProvider.class.getName();
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @ParametersAreNonnullByDefault
  static class MyProvider implements WebDriverProvider {
    static URL url;

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
      RemoteWebDriver webDriver = new RemoteWebDriver(url, chromeOptions(null));
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }
  }
}
