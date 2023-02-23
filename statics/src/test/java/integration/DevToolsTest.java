package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v109.emulation.Emulation;

import java.util.Optional;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DevToolsTest {
  @Test
  void issue2162() {
    open();
    DevTools devTools = ((HasDevTools) WebDriverRunner.getWebDriver()).getDevTools();
    devTools.createSession();
    devTools.send(Emulation.setGeolocationOverride(Optional.of(55.5), Optional.of(66.6), Optional.of(77.7)));
    open("https://www.gps-coordinates.net/my-location");
    $("h2").shouldHave(text("What are my coordinates?"));
    $("#lat").shouldHave(text("55.500000 / N 55° 30' 0''"));
    $("#lng").shouldHave(text("66.600000 / E 66° 35' 59.999''"));
  }
}
