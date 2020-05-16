package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static java.lang.System.nanoTime;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class ChromeProfileByFactoryTest extends IntegrationTest {
  private static final Logger logger = LoggerFactory.getLogger(ChromeProfileByFactoryTest.class);
  private static final File downloadsFolder = new File(Configuration.downloadsFolder);
  private static final File chromedriverLog = new File(downloadsFolder, "chromedriver." + nanoTime());

  @BeforeEach
  void setUp() throws IOException {
    assumeThat(isChrome()).isTrue();

    closeWebDriver();
    Configuration.timeout = 1000;
    Configuration.browser = MyFactory.class.getName();
    FileUtils.write(chromedriverLog, "", UTF_8);
    logger.info("Chrome log: {}", chromedriverLog.getAbsolutePath());
  }

  @Test
  void downloadsFilesToCustomFolder() throws IOException {
    openFile("page_with_uploads.html");
    $(byText("Download me")).shouldBe(visible);

    String log = readFileToString(chromedriverLog, UTF_8);
    assertThat(log).contains("\"excludeSwitches\": [ \"enable-automation\" ]");
    assertThat(log).contains("\"extensions\": [  ]");
    assertThat(log).contains("\"credentials_enable_service\": false");
    assertThat(log).contains("\"download.default_directory\": \"" + downloadsFolder.getAbsolutePath() + "\"");

    String arguments = "\"--proxy-bypass-list=\\u003C-loopback>\", \"--no-sandbox\", \"--disable-3d-apis\"";
    if (Configuration.headless) {
      assertThat(log).contains("\"args\": [ \"--headless\", \"--disable-gpu\", " + arguments + " ]");
    }
    else {
      assertThat(log).contains("\"args\": [ " + arguments + " ]");
    }
  }

  private static class MyFactory extends ChromeDriverFactory {
    @Override
    public WebDriver create(Config config, Browser browser, Proxy proxy) {
      System.setProperty("webdriver.chrome.logfile", chromedriverLog.getAbsolutePath());
      System.setProperty("webdriver.chrome.verboseLogging", "true");
      return super.create(config, browser, proxy);
    }

    @Override
    protected ChromeOptions createChromeOptions(Config config, Browser browser, Proxy proxy) {
      ChromeOptions options = super.createChromeOptions(config, browser, proxy);
      options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
      options.addArguments(asList("--no-sandbox", "--disable-3d-apis"));

      return options;
    }
  }
}
