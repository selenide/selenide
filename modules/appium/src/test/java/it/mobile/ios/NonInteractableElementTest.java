package it.mobile.ios;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.appium.SelenideAppium.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NonInteractableElementTest extends BaseSwagLabsAppIosTest {
  private final SelenideAppiumElement element = $(By.name("Add To Cart button"));

  @BeforeEach
  void makeElementNonInteractable() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://product-details/1");
    SelenideAppium.$(By.name("counter minus button")).tap();
    Configuration.timeout = 10;
  }

  @Test
  @Disabled("There are no Non Interactable Elements")
  void clickMissingElement() {
    assertThatThrownBy(() -> element.click())
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {By.name: Add To Cart button}")
      .hasMessageContaining("Element: '<XCUIElementTypeOther")
      .hasMessageContaining("visible=\"false\"")
      .hasMessageContaining("Actual value: hidden");
  }

  @Test
  @Disabled("There are no Non Interactable Elements")
  void clearMissingElement() {
    assertThatThrownBy(() -> element.clear())
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {By.name: Add To Cart button}")
      .hasMessageContaining("Element: '<XCUIElementTypeOther")
      .hasMessageContaining("visible=\"false\"")
      .hasMessageContaining("Actual value: hidden");
  }
}

