package integration.ios;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.appium.AppiumSelectors.byAttribute;

class DeepLinkUrlIosTest extends BaseSauceLabAppIosTest {

  @Test
  void testDeepLinkInIos() {
    SelenideAppium.launchDeepLink("mydemoapprn://product-details/1",
                                  "com.saucelabs.mydemoapp.rn");
    $(byAttribute("label", "Sauce Labs Backpack")).shouldBe(visible);
  }
}
