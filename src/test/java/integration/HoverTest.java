package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.HoverOptions.withOffset;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hover with offset - calculates offset from the center of the element.
 * Element '#hoverable' is 800x401 -> its center is 400x200.
 * Hover to (400+123, 200+122) -> (523, 322)
 */
final class HoverTest extends ITest {

  private static final String REGEX_COORDINATES = "\\((\\d{3}), (\\d{3})\\)";

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("hover.html");
  }

  @Test
  void canEmulateHover() {
    $("#hoverable").hover().shouldHave(text("It's hover"));
    $("#coords").should(matchText("\\(\\d{3}, \\d{3}\\)"));
    verifyCoordinates($("#coords").text());

    $("h1").hover();
    $("#hoverable").shouldHave(text("It's not hover"));
    $("#coords").shouldHave(exactText(""));
  }

  private void verifyCoordinates(String coordinatesAsText) {
    int x = parseInt(coordinatesAsText.replaceFirst(REGEX_COORDINATES, "$1"));
    int y = parseInt(coordinatesAsText.replaceFirst(REGEX_COORDINATES, "$2"));
    assertThat(x).isBetween(300, 500);
    assertThat(y).isBetween(150, 250);
  }

  @Test
  void hoverWithOffset() {
    $("#hoverable").hover(withOffset(123, 122)).shouldHave(text("It's hover"));
    $("#coords").shouldHave(text("(523, 322)"));
  }
}
