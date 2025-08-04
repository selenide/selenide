package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.WebDriverInstance;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

import static com.codeborne.selenide.BrowserDownloadsFolder.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WebdriversRegistryTest {
  @Test
  void register_unregister() {
    RemoteWebDriver wd1 = mock();
    RemoteWebDriver wd2 = mock();
    WebDriverInstance driver1 = new WebDriverInstance(-1L, new SelenideConfig(), wd1, null, from(new File("/tmp/t1")));
    WebDriverInstance driver2 = new WebDriverInstance(-2L, new SelenideConfig(), wd2, null, from(new File("/tmp/t2")));
    WebdriversRegistry.register(driver1);
    WebdriversRegistry.register(driver2);

    assertThat(WebdriversRegistry.webdriver(-1L).map(WebDriverInstance::webDriver)).contains(wd1);
    assertThat(WebdriversRegistry.webdriver(-2L).map(WebDriverInstance::webDriver)).contains(wd2);

    WebdriversRegistry.unregister(driver1);
    assertThat(WebdriversRegistry.webdriver(-1L)).isEmpty();
    assertThat(WebdriversRegistry.webdriver(-2L).map(WebDriverInstance::webDriver)).contains(wd2);

    WebdriversRegistry.unregister(driver2);
    assertThat(WebdriversRegistry.webdriver(-1L)).isEmpty();
    assertThat(WebdriversRegistry.webdriver(-2L)).isEmpty();
  }
}
