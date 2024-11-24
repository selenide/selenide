package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.inNewBrowser;
import static com.codeborne.selenide.WebDriverConditions.cookie;

public class Issue2859Test {
  private static final Logger log = LoggerFactory.getLogger("Issue2859");

  @Test
  public void selenide751failed1() {
    final SelenideConfig original = new SelenideConfig();
    Selenide.open("https://duckduckgo.com", original);
    WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("bober", "kurwa"));
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));
    inNewBrowser(() -> {
      SelenideConfig target = new SelenideConfig().browser("firefox");
      Selenide.open("https://google.com", target);
      Selenide.webdriver().shouldNotHave(cookie("bober", "kurwa"));
    });
    Selenide.open("https://duckduckgo.com", original);
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));
  }

  @Test
  public void justClose() {
    final SelenideConfig original = new SelenideConfig();
    log.info("Open browser #1 {}", original);
    Selenide.open("https://duckduckgo.com?step=1", original);
    WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("bober", "kurwa"));
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));

    log.info("inNewBrowser #2");

    inNewBrowser(() -> {
      log.info("inNewBrowser -> close #3");
      Selenide.closeWebDriver();
    });

    log.info("Open browser #2 {}", original);

    Selenide.open("https://duckduckgo.com?step=3", original);
    log.info("Opened browser #3 {}", original);
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));
  }

  @Test
  public void withoutConfig() {
    log.info("Open browser #1");
    Selenide.open("https://duckduckgo.com?step=1");
    WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("bober", "kurwa"));
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));

    log.info("inNewBrowser #2");

    inNewBrowser(() -> {
      log.info("inNewBrowser -> close #3");
      Selenide.open("https://ru.selenide.org?step=2");
      Selenide.webdriver().shouldNotHave(cookie("bober", "kurwa"));
      Selenide.closeWebDriver();
    });

    log.info("Open browser #2");

    Selenide.open("https://duckduckgo.com?step=3");
    log.info("Opened browser #3");
    Selenide.webdriver().shouldHave(cookie("bober", "kurwa"));
  }
}
