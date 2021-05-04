package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.HoverOptions.withOffset;

/**
 * Hover with offset - calculates offset from the center of the element.
 * Element '#hoverable' is 800x401 -> its center is 400x200.
 * Hover to (400+123, 200+122) -> (523, 322)
 */
final class HoverTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("hover.html");
  }

  @Test
  void canEmulateHover() {
    $("#hoverable").hover().shouldHave(text("It's hover"));
    $("#coords").shouldHave(text("(400, 200)"));

    $("h1").hover();
    $("#hoverable").shouldHave(text("It's not hover"));
    $("#coords").shouldHave(exactText(""));
  }

  @Test
  void hoverWithOffset() {
    $("#hoverable").hover(withOffset(123, 122)).shouldHave(text("It's hover"));
    $("#coords").shouldHave(text("(523, 322)"));
  }
}
