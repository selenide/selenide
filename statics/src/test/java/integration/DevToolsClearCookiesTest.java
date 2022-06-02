package integration;

import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class DevToolsClearCookiesTest extends IntegrationTest {
  @BeforeEach
  void addCookiesBeforeTest() throws MalformedURLException {
    assumeThat(isChrome()).isTrue();
    open("/start_page.html");

    String domain = new URL(getWebDriver().getCurrentUrl()).getHost();
    getWebDriver().manage().addCookie(new Cookie("username", "John Doe", domain, "/", null));
    Assumptions.assumeFalse(getCookies().isEmpty());
  }

  @Test
  void test() {
    open("https://www.github.com/");
    devTools().clearCookies();
    open("/start_page.html");
    assertThat(getCookies()).isEmpty();
  }

  private Collection<Cookie> getCookies() {
    return getWebDriver().manage().getCookies()
      .stream().filter(it -> !it.getName().equals("session_id")).collect(Collectors.toList());
  }
}
