package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v144.network.Network;
import org.openqa.selenium.devtools.v144.network.model.NetworkConditions;

import java.util.List;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assumptions.assumeThat;

final class OfflineModeTest extends ITest {

  private static final List<NetworkConditions> ALL_REQUESTS =
    List.of(new NetworkConditions("", 0, 0, 0, empty(), empty(), empty(), empty()));

  @BeforeEach
  void openTestPage() {
    openFile("file_upload_form.html");
  }

  @Test
  void canToggleOfflineMode_chromium() {
    assumeThat(isChrome() || isEdge()).isTrue();

    ChromiumNetworkConditions networkConditions = new ChromiumNetworkConditions();
    networkConditions.setOffline(true);
    HasNetworkConditions networkConditionsDriver = (HasNetworkConditions) driver().getWebDriver();

    $("h1").shouldHave(text("File upload form"));

    networkConditionsDriver.setNetworkConditions(networkConditions);
    driver().refresh();

    $("h1").shouldHave(text("no internet").or(text("not connected")).or(disappear));

    networkConditionsDriver.setNetworkConditions(new ChromiumNetworkConditions());
    driver().refresh();
    $("h1").shouldHave(text("File upload form"));
  }

  @Test
  void canToggleOfflineMode_devTools() {
    assumeThat(isChrome() || isEdge()).isTrue();

    String windowHandle = driver().getWebDriver().getWindowHandle();
    DevTools devTools = ((HasDevTools) driver().getWebDriver()).getDevTools();
    devTools.createSessionIfThereIsNotOne(windowHandle);

    // Enable network emulation
    devTools.send(Network.enable(empty(), empty(), empty(), empty(), empty()));

    $("h1").shouldHave(text("File upload form"));

    // Go offline
    devTools.send(Network.emulateNetworkConditionsByRule(true, ALL_REQUESTS));

    driver().refresh();

    $("h1").shouldHave(text("no internet").or(text("not connected")).or(disappear));

    // Bring back online
    devTools.send(Network.emulateNetworkConditionsByRule(false, ALL_REQUESTS));
    driver().refresh();
    $("h1").shouldHave(text("File upload form"));
  }
}
