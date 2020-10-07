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

  protected void setTimeout(long timeoutMs) {
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

  protected SelenideDriver driver() {
    return driver.get();
  }

  protected SelenideElement $(String locator) {
    return driver().$(locator);
  }

  protected SelenideElement $(String locator, int index) {
    return driver().$(locator, index);
  }

  protected SelenideElement $(By locator) {
    return driver().$(locator);
  }

  protected SelenideElement $(By locator, int index) {
    return driver().$(locator, index);
  }

  protected SelenideElement $x(String locator) {
    return driver().$x(locator);
  }

  protected ElementsCollection $$(String locator) {
    return driver().$$(locator);
  }

  protected ElementsCollection $$(By locator) {
    return driver().$$(locator);
  }

  protected ElementsCollection $$x(String locator) {
    return driver().$$x(locator);
  }

  protected SelenideTargetLocator switchTo() {
    return driver().switchTo();
  }

  protected void openFile(String fileName) {
    driver().open("/" + fileName + "?browser=" + browser +
      "&timeout=" + driver().config().timeout());
  }
}
