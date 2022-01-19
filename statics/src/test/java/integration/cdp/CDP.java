package integration.cdp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.decorators.Decorated;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

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

  @Nonnull
  @CheckReturnValue
  static HasDevTools getCdpDriver() {
    WebDriver webDriver = getWebDriver();
    return getCdpDriver(webDriver);
  }

  @Nonnull
  @CheckReturnValue
  private static HasDevTools getCdpDriver(WebDriver webDriver) {
    if (webDriver instanceof HasDevTools) {
      return ((HasDevTools) webDriver);
    }
    else if (webDriver instanceof Decorated) {
      return getCdpDriver(((Decorated<WebDriver>) webDriver).getOriginal());
    }
    else if (webDriver instanceof WrapsDriver) {
      return getCdpDriver(((WrapsDriver) webDriver).getWrappedDriver());
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

  static void toggleOffline(boolean offline) {
    ChromiumNetworkConditions networkConditions = new ChromiumNetworkConditions();
    networkConditions.setOffline(offline);
    getNetworkConditionsDriver().setNetworkConditions(networkConditions);
  }

  @FunctionalInterface
  public interface Consumer<T> {
    void accept(T t) throws Exception;
  }
}
