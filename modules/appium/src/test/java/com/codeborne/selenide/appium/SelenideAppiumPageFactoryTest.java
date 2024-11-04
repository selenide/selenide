package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;

class SelenideAppiumPageFactoryTest {

  @Test
  void exception_on_init_mobile_element_without_webdriver() {
    Selenide.closeWebDriver();
    assertThatThrownBy(() -> Selenide.page(PageWithPlatformSelectors.class))
      .isInstanceOf(WebDriverException.class)
        .hasMessageStartingWith("The SelenideAppiumPageFactory requires a webdriver instance to be created before page " +
          "initialization; No webdriver is bound to current thread. You need to call open() first");
  }

  @Test
  void web_element_page_factory_doesnt_require_webdriver_instance() {
    Selenide.closeWebDriver();
    var page = Selenide.page(PageWithWebSelectors.class);
    assertThat(page.element).isNotNull()
      .hasToString("{By.id: someId}");
  }

  @Test
  void mobile_platform_element_successfully_init_with_created_webdriver() {
    AndroidDriver androidDriver = mock(AndroidDriver.class);
    when(androidDriver.getCapabilities()).thenReturn(new DesiredCapabilities());
    WebDriverRunner.setWebDriver(androidDriver);
    var page = Selenide.page(PageWithPlatformSelectors.class);
    assertThat(page.element).isNotNull()
      .hasToString("{By.id: element}");
  }

  private static class PageWithPlatformSelectors {
    @AndroidFindBy(id = "element")
    public SelenideElement element;
  }

  private static class PageWithWebSelectors {
    @FindBy(id = "someId")
    public SelenideElement element;
  }
}
