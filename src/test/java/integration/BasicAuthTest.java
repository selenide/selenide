package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.source;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuthInFirefox() {
    Assumptions.assumeTrue(isFirefox());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source())
      .contains("\"authenticated\": true,");
  }

  @Test
  void canPassBasicAuthInHtmlUnit() {
    Assumptions.assumeTrue(isHtmlUnit());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    assertThat(source())
      .contains("\"authenticated\":true,");
  }

  @Test
  @Disabled
  void canPassBasicAuthInPhantomJS() {
    Assumptions.assumeTrue(isPhantomjs());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source())
      .contains("\"authenticated\": true,");
  }

  @Test
  @Disabled
  void canPassBasicAuthInChrome() {
    Assumptions.assumeTrue(isChrome());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    $(By.tagName("pre")).waitUntil(visible, 10000);
    assertThat(source())
      .contains("\"authenticated\": true,");
  }

  @Test
  @Disabled
  void canPassBasicAuthInIe() {
    Assumptions.assumeTrue(isIE());
    Selenide.open("http://httpbin.org/basic-auth/user/passwd",
      "",
      "user",
      "passwd");
    assertThat(source())
      .contains("WebDriver");
  }
}
