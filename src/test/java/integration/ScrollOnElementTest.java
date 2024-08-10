package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;

final class ScrollOnElementTest extends ITest {
  private final SelenideElement scrollableDiv = $("#scrollable-div");
  private final SelenideElement scrollableDivHorizontal = $("#scrollable-div2");
  private final SelenideElement hiddenButton = $("#button");
  private final SelenideElement hiddenButtonHorizontal = $("#button2");

  @BeforeEach
  void openTestPage() {
    openFile("page_with_scroll_element.html");
  }

  @Test
  void userCanScrollDownSpecificElement() {
    hiddenButton.shouldBe(hidden);
    scrollableDiv.scrollOnElement(1000);
    hiddenButton.shouldBe(visible);
  }

  @Test
  void userCanScrollSpecificElementToTheRight() {
    hiddenButtonHorizontal.shouldBe(hidden);
    scrollableDivHorizontal.scrollOnElement(0, 2000);
    hiddenButtonHorizontal.shouldBe(visible);
  }
}
