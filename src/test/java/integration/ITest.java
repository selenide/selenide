package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideTargetLocator;
import org.openqa.selenium.By;

import static java.lang.ThreadLocal.withInitial;

public class ITest extends BaseIntegrationTest {
  private static final ThreadLocal<SelenideDriver> driver = withInitial(() ->
    new SelenideDriver(new SelenideConfig().browser(browser).baseUrl(getBaseUrl())));

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

  <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return driver().open("/" + fileName + "?browser=" + browser +
      "&timeout=" + driver().config().timeout(), pageObjectClass);
  }
}
