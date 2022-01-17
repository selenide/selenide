package integration.cdp;

import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;

import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static integration.cdp.CDP.getNetworkConditionsDriver;
import static integration.cdp.CDP.runWithDevTools;
import static org.assertj.core.api.Assumptions.assumeThat;

final class ChromiumNetworkConditionsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge()).isTrue();
  }

  @Test
  void canAddListenersToNetworkRequests() throws Exception {
    openFile("file_upload_form.html");

    runWithDevTools(devTools -> {
      ChromiumNetworkConditions networkConditions = new ChromiumNetworkConditions();
      networkConditions.setOffline(true);
      getNetworkConditionsDriver().setNetworkConditions(networkConditions);

      $("h1").shouldHave(text("File upload form"));

      refresh();

      $("h1").shouldHave(or("no network", text("no internet"), text("not connected")));
    });
  }
}
