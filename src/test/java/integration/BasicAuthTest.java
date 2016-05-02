package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;

public class BasicAuthTest extends IntegrationTest {

  private String browserOriginalValue;

  @Before
  public void setUp() {
    browserOriginalValue = Configuration.browser;
  }

  @After
  public void tearDown() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = browserOriginalValue;
  }

  @Test
  public void canPassBasicAuthInFirefox() {
    Configuration.browser = "firefox";
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  public void canPassBasicAuthInHtmlUnit() {
    Configuration.browser = "htmlunit";
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  @Ignore
  public void canPassBasicAuthInPhantom() {
    Configuration.browser = "phantomjs";
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  @Ignore
  public void canPassBasicAuthInChrome() {
    Configuration.browser = "chrome";
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  @Ignore
  public void canPassBasicAuthInIe() {
    Configuration.browser = "ie";
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    assertThat(source(), containsString("WebDriver"));
  }

  public boolean isAlertPresent() {
    try {
      Wait().until(alertIsPresent());
      return true;
    }
    catch (TimeoutException | NoAlertPresentException ex) {
      return false;
    }
  }
}
