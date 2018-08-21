package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Configuration.FileDownloadMode;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class BasicAuthTest extends IntegrationTest {
  @Test
  void canPassBasicAuth() {
    open("/basic-auth/hello", "", "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @Test
  void canAuthUsingProxy() {
    Configuration.fileDownload = FileDownloadMode.PROXY;
    open("/basic-auth/hello", AuthenticationType.BASIC, "scott", "tiger");
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }
}
