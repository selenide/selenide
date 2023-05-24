package it.mobile.ios;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.AppiumCondition;
import com.codeborne.selenide.appium.AppiumSelectors;
import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.appium.AppiumCollectionCondition.attributes;
import static com.codeborne.selenide.appium.conditions.CombinedAttribute.android;

class IosAppiumConditionsTest extends BaseSwagLabsAppIosTest {

  @Test
  void testAppiumCollectionConditionAttribute() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    $$(AppiumSelectors.byAttribute("type", "XCUIElementTypeTextField"))
      .shouldBe(size(1))
      .shouldHave(attributes(android("text").ios("name"), "Username input field"));
  }

  @Test
  void testAppiumConditionAttribute() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    SelenideElement element = $(AppiumSelectors.byAttribute("type", "XCUIElementTypeTextField"));

    // Selenide style:
    element.shouldHave(attribute("name", "Username input field"));

    // Selenide-Appium style:
    element.shouldHave(AppiumCondition.attribute(android("").ios("name"), "Username input field"));
  }
}
