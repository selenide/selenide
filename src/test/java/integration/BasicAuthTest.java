package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.source;
import static java.util.regex.Pattern.DOTALL;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuthInFirefox() {
    assumeTrue(isFirefox());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThatBasicAuthSucceeded();
  }

  @Test
  void canPassBasicAuthInHtmlUnit() {
    assumeTrue(isHtmlUnit());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    assertThatBasicAuthSucceeded();
  }

  @Test
  @Disabled
  void canPassBasicAuthInPhantomJS() {
    assumeTrue(isPhantomjs());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThatBasicAuthSucceeded();
  }

  @Test
  @Disabled
  void canPassBasicAuthInChrome() {
    assumeTrue(isChrome());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThatBasicAuthSucceeded();
  }

  @Test
  @Disabled
  void canPassBasicAuthInIe() {
    assumeTrue(isIE());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    assertThat(source())
      .contains("WebDriver");
  }

  private void assertThatBasicAuthSucceeded() {
    Pattern regex = Pattern.compile("\\{.*\"authenticated\":\\s*true.*", DOTALL);
    assertThat(source()).matches(regex);
  }
}
