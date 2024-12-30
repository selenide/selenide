package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.ScrollDirection.*;
import static com.codeborne.selenide.ScrollOptions.defaultScrollOptions;
import static com.codeborne.selenide.ScrollOptions.direction;

final class ScrollTest extends ITest {
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
    scrollableDivDown.scroll(defaultScrollOptions());
    hiddenButtonDown.shouldBe(visible);
  }

  @Test
  void userCanScrollSpecificElementToTheRight() {
    hiddenButtonRight.shouldBe(hidden);
    scrollableDivRight.scroll(direction(RIGHT).distance(2000));
    hiddenButtonRight.shouldBe(visible);
  }

  @Test
  void userCanScrollUpSpecificElement() {
    hiddenButtonUp.shouldBe(hidden);
    scrollableDivUp.scroll(direction(UP));
    hiddenButtonUp.shouldBe(visible);
  }

  @Test
  void userCanScrollSpecificElementToTheLeft() {
    hiddenButtonLeft.shouldBe(hidden);
    scrollableDivLeft.scroll(direction(LEFT).distance(1400));
    hiddenButtonLeft.shouldBe(visible);
  }
}
