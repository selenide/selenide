package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideTargetLocator;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v135.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static java.lang.ThreadLocal.withInitial;

public abstract class ITest extends BaseIntegrationTest {
  private static final Logger browserLogs = LoggerFactory.getLogger("browser");
  private final long longTimeout = Long.parseLong(System.getProperty("selenide.timeout", "4000"));

  private static final ThreadLocal<SelenideConfig> config = withInitial(() ->
    new SelenideConfig().browser(browser).baseUrl(getBaseUrl()).timeout(1));

  private static final ThreadLocal<SelenideDriver> driver = withInitial(() ->
    new SelenideDriver(config()));

  @BeforeEach
  final void resetDefaults() {
    resetShortTimeout();
    config().textCheck(PARTIAL_TEXT);
  }

  final void resetShortTimeout() {
    config.get().timeout(1);
  }

  protected static SelenideConfig config() {
    return config.get();
  }

  protected final void setTimeout(long timeoutMs) {
    config.get().timeout(timeoutMs);
  }

  protected final void withLongTimeout(Runnable test) {
    config.get().timeout(longTimeout);
    try {
      test.run();
    }
    finally {
      resetShortTimeout();
    }
  }

  protected final void withFastSetValue(Runnable test) {
    config.get().fastSetValue(true);
    try {
      test.run();
    }
    finally {
      config.get().fastSetValue(false);
    }
  }

  protected final SelenideDriver driver() {
    return driver.get();
  }

  protected final SelenideElement $(String locator) {
    return driver().$(locator);
  }

  protected final SelenideElement $(String locator, int index) {
    return driver().$(locator, index);
  }

  protected final SelenideElement $(By locator) {
    return driver().$(locator);
  }

  protected final SelenideElement $(By locator, int index) {
    return driver().$(locator, index);
  }

  protected final SelenideElement $x(String locator) {
    return driver().$x(locator);
  }

  protected final ElementsCollection $$(String locator) {
    return driver().$$(locator);
  }

  protected final ElementsCollection $$(By locator) {
    return driver().$$(locator);
  }

  protected final ElementsCollection $$x(String locator) {
    return driver().$$x(locator);
  }

  protected final SelenideTargetLocator switchTo() {
    return driver().switchTo();
  }

  protected final void openFile(String fileName) {
    retry(() -> {
      if (driver().hasWebDriverStarted()) {
        driver().open("about:blank");
      }
      else {
        driver().open();
        cast(driver().getWebDriver(), HasDevTools.class).ifPresent(webdriver -> {
          var devTools = webdriver.getDevTools();
          devTools.createSessionIfThereIsNotOne();
          devTools.send(Log.enable());
          devTools.addListener(Log.entryAdded(), log ->
            browserLogs.info("[{}] {} source:{} url:{}", log.getLevel(), log.getText(), log.getSource(), log.getUrl().orElse("-"))
          );
        });
      }
      driver().open("/" + fileName + "?browser=" + browser +
                    "&timeout=" + driver().config().timeout());
    }, 5);
  }
}
