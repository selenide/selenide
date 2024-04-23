package it.mobile.ios;

import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.appium.SelenideAppium.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NonInteractableElementTest extends BaseIosCalculatorTest {
  private final SelenideAppiumElement element = $(By.name("ComputeSumButton"));

  @BeforeEach
  void makeElementNonInteractable() {
    $(By.name("show alert")).click();
  }

  @Test
  void clickMissingElement() {
    assertThatThrownBy(() -> element.click())
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {By.name: ComputeSumButton}")
      .hasMessageContaining("Element: '<XCUIElementTypeButton")
      .hasMessageContaining("visible=\"false\"")
      .hasMessageContaining("Actual value: hidden");
  }

  @Test
  void clearMissingElement() {
    assertThatThrownBy(() -> element.clear())
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {By.name: ComputeSumButton}")
      .hasMessageContaining("Element: '<XCUIElementTypeButton")
      .hasMessageContaining("visible=\"false\"")
      .hasMessageContaining("Actual value: hidden");
  }
}

