package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.ScrollByDirection.*;
import static com.codeborne.selenide.ScrollByOptions.defaultScrollByOptions;
import static com.codeborne.selenide.ScrollByOptions.direction;

final class ScrollByTest extends ITest {
  private final SelenideElement scrollableDivDown = $("#scrollable-div");
  private final SelenideElement scrollableDivRight = $("#scrollable-div2");
  private final SelenideElement scrollableDivUp = $("#scrollable-div3");
  private final SelenideElement scrollableDivLeft = $("#scrollable-div4");
  private final SelenideElement hiddenButtonDown = $("#button");
  private final SelenideElement hiddenButtonRight = $("#button2");
  private final SelenideElement hiddenButtonUp = $("#button3");
  private final SelenideElement hiddenButtonLeft = $("#button4");


  @BeforeEach
  void openTestPage() {
    openFile("page_with_scroll_element.html");
  }

  @Test
  void userCanScrollDownSpecificElement() {
    hiddenButtonDown.shouldBe(hidden);
    scrollableDivDown.scrollBy(defaultScrollByOptions());
    hiddenButtonDown.shouldBe(visible);
  }

  @Test
  void userCanScrollSpecificElementToTheRight() {
    hiddenButtonRight.shouldBe(hidden);
    scrollableDivRight.scrollBy(direction(RIGHT).distance(2000));
    hiddenButtonRight.shouldBe(visible);
  }

  @Test
  void userCanScrollUpSpecificElement() {
    hiddenButtonUp.shouldBe(hidden);
    scrollableDivUp.scrollBy(direction(UP));
    hiddenButtonUp.shouldBe(visible);
  }

  @Test
  void userCanScrollSpecificElementToTheLeft() {
    hiddenButtonLeft.shouldBe(hidden);
    scrollableDivLeft.scrollBy(direction(LEFT).distance(1400));
    hiddenButtonLeft.shouldBe(visible);
  }
}
