package integration.cdp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.Augmenter;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

class CDP {
  static void runWithDevTools(Consumer<DevTools> block) throws Exception {
    DevTools devTools = getDevTools();
    devTools.createSession();

    try {
      block.accept(devTools);
    }
    finally {
      devTools.disconnectSession();
    }
  }

  private static DevTools getDevTools() {
    return getCdpDriver().getDevTools();
  }

  static HasDevTools getCdpDriver() {
    WebDriver webDriver = getWebDriver();
    if (webDriver instanceof HasDevTools) {
      return ((HasDevTools) webDriver);
    }
    else {
      WebDriver augmentedDriver = new Augmenter().augment(getWebDriver());
      return ((HasDevTools) augmentedDriver);
    }
  }

  static HasNetworkConditions getNetworkConditionsDriver() {
    WebDriver webDriver = getWebDriver();
    if (webDriver instanceof HasNetworkConditions) {
      return ((HasNetworkConditions) webDriver);
    }
    else {
      WebDriver augmentedDriver = new Augmenter().augment(getWebDriver());
      return ((HasNetworkConditions) augmentedDriver);
    }
  }

  @FunctionalInterface
  public interface Consumer<T> {
    void accept(T t) throws Exception;
  }
}
