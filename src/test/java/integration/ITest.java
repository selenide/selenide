package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideTargetLocator;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;

import static java.lang.ThreadLocal.withInitial;

public abstract class ITest extends BaseIntegrationTest {
  private final long longTimeout = Long.parseLong(System.getProperty("selenide.timeout", "4000"));

  private static final ThreadLocal<SelenideConfig> config = withInitial(() ->
    new SelenideConfig().browser(browser).baseUrl(getBaseUrl()).timeout(1));

  private static final ThreadLocal<SelenideDriver> driver = withInitial(() ->
    new SelenideDriver(config.get()));

  @BeforeEach
  final void resetShortTimeout() {
    config.get().timeout(1);
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
      driver().open("/" + fileName + "?browser=" + browser +
                    "&timeout=" + driver().config().timeout());
    }, 5);
  }
}
