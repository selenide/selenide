package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Selenide.$;

/**
 * Click with offset - calculates offset from the center of clicked element.
 * Element '#page' is 800x600 -> its center is 400x300.
 * Click to (400+123, 300+222) -> (523, 522)
 */
final class ClickRelativeTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_relative_click_position.html");
  }

  @Test
  void userCanClickElementWithOffsetPosition_withActions() {
    Configuration.clickViaJs = false;

    $("#page").click(123, 222);

    $("#coords").should(matchText("(523, 522)"));
  }

  @Test
  void userCanClickElementWithOffsetPosition_withJavascript() {
    Configuration.clickViaJs = true;

    $("#page").click(123, 222);

    $("#coords").should(matchText("(523, 522)"));
  }

  @Test
  void userCanClickElementWithOffsetPositions_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offset(123, 222));

    $("#coords").should(matchText("(523, 522)"));
  }

  @Test
  void userCanClickElementWithOffsetXPosition_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offsetX(123));

    $("#coords").should(matchText("(523, 300)"));
  }
}
