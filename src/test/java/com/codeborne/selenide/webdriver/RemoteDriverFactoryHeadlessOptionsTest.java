package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import java.util.List;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterTest;

import static com.codeborne.selenide.webdriver.SeleniumCapabilitiesHelper.getBrowserLaunchArgs;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class RemoteDriverFactoryHeadlessOptionsTest {

  private RemoteDriverFactory factory = new RemoteDriverFactory();
  private Proxy proxy = mock(Proxy.class);

  @AfterTest
  public void tearDown() {
    Configuration.browser = null;
    Configuration.headless = false;
  }

  @Test
  public void shouldNotAddChromeHeadlessOptions() throws Exception {
    Configuration.headless = true;
    Configuration.browser = "chrome";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    assertThat(launchArguments, hasItems("--headless"));
    assertThat(launchArguments, hasItems("--disable-gpu"));
  }

  @Test
  public void shouldNotAddFirefoxHeadlessOptions() throws Exception {
    Configuration.headless = true;
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments, hasItems("-headless"));
  }

  @Test
  public void shouldAddChromeHeadlessOptions() throws Exception {
    Configuration.browser = "chrome";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(ChromeOptions.CAPABILITY, headlessCapabilities);

    assertThat(launchArguments, not(hasItems("--headless")));
    assertThat(launchArguments, not(hasItems("--disable-gpu")));
  }

  @Test
  public void shouldAddFirefoxHeadlessOptions() throws Exception {
    Configuration.browser = "firefox";

    Capabilities headlessCapabilities = factory.getDriverCapabilities(proxy);
    List<String> launchArguments = getBrowserLaunchArgs(FirefoxOptions.FIREFOX_OPTIONS, headlessCapabilities);

    assertThat(launchArguments, not(hasItems("-headless")));
  }

}
