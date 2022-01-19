package integration.cdp;

import integration.IntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static integration.cdp.CDP.runWithDevTools;
import static org.assertj.core.api.Assumptions.assumeThat;

final class ChromiumNetworkConditionsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge()).isTrue();
  }

  @AfterAll
  static void afterAll() {
    closeWebDriver();
  }

  @Test
  void canAddListenersToNetworkRequests() throws Exception {
    openFile("file_upload_form.html");

    runWithDevTools(devTools -> {
      $("h1").shouldHave(text("File upload form"));

      CDP.toggleOffline(true);
      refresh();
      $("h1").shouldHave(or("no network", text("no internet"), text("not connected")));

      CDP.toggleOffline(false);
      refresh();
      $("h1").shouldHave(text("File upload form"));
    });
  }
}
