package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.net.URL;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverFactoryWithRemoteBrowser extends AbstractGridTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome()).isTrue();
  }

  @Test
  void customWebdriverProviderCanUseRemoteWebdriver() {
    MyFactory.url = gridUrl;
    Configuration.browser = MyFactory.class.getName();
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @ParametersAreNonnullByDefault
  static class MyFactory extends ChromeDriverFactory {
    static URL url;

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
      RemoteWebDriver webDriver = new RemoteWebDriver(url, chromeOptions(proxy));
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }
  }
}
