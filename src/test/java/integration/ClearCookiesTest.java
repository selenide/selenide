package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;

import java.util.Set;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClearCookiesTest extends IntegrationTest {
  @Before
  public void addCookiesBeforeTest() {
    getWebDriver().manage().addCookie(new Cookie("username", "John Doe"));
    Set<Cookie> cookieSet = getWebDriver().manage().getCookies();
    assertFalse(cookieSet.isEmpty());
  }


  @Test
  public void clearCookieTest() {
    clearBrowserCookies();
    Set<Cookie> cookieSet = getWebDriver().manage().getCookies();
    assertTrue(cookieSet.isEmpty());
  }
}
