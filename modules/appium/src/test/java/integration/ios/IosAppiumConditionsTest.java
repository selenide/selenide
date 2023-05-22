package integration.ios;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.appium.AppiumCollectionCondition;
import com.codeborne.selenide.appium.AppiumCondition;
import com.codeborne.selenide.appium.AppiumSelectors;
import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;

class IosAppiumConditionsTest extends BaseSwagLabsAppIosTest {

  @Test
  void testAppiumCollectionConditionAttribute() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    $$(AppiumSelectors.byAttribute("type", "XCUIElementTypeTextField"))
      .shouldBe(CollectionCondition.size(1))
      .shouldHave(AppiumCollectionCondition
                    .exactAttributes("text", "name", "Username input field"));
  }

  @Test
  void testAppiumConditionAttribute() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    $(AppiumSelectors.byAttribute("type", "XCUIElementTypeTextField"))
      .shouldHave(AppiumCondition.iosAttributeWithValue("name", "Username input field"))
      .shouldHave(AppiumCondition.attributeWithValue("", "name", "Username input field"));
  }
}
