package integration;

import com.codeborne.selenide.Selenide;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

public class BasicAuthTest extends IntegrationTest {

  @Test
  public void canPassBasicAuthInFirefox() {
    assumeTrue(isFirefox());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  public void canPassBasicAuthInHtmlUnit() {
    assumeTrue(isHtmlUnit());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    assertThat(source(), containsString("\"authenticated\": true,"));
  }

  @Test
  @Ignore
  public void canPassBasicAuthInPhantomJS() {
    assumeTrue(isPhantomjs());
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
    assumeTrue(isChrome());
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
    assumeTrue(isIE());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
        "",
        "user",
        "passwd");
    assertThat(source(), containsString("WebDriver"));
  }
}
