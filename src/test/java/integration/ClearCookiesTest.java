package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class ClearCookiesTest extends IntegrationTest {
  @Before
  public void addCookiesBeforeTest() throws MalformedURLException {
    String domain = new URL(getWebDriver().getCurrentUrl()).getHost();
    getWebDriver().manage().addCookie(new Cookie("username", "John Doe", domain, "/", null));
    Set<Cookie> cookieSet = getWebDriver().manage().getCookies();
    assumeFalse(cookieSet.isEmpty());
  }


  @Test
  public void clearCookieTest() {
    clearBrowserCookies();
    Set<Cookie> cookieSet = getWebDriver().manage().getCookies();
    assertTrue(cookieSet.isEmpty());
  }
}
