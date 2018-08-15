package integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.zoom;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class ZoomTest extends IntegrationTest {
  @Test
  void canZoomInAndOut() {
    assumeFalse(isPhantomjs() || isHtmlUnit(), "Both browsers return the same (X,Y) even after successful zooming");

    openFile("page_with_big_divs.html");
    int initialX = $("#wide_div").getLocation().getX();
    assertBetween($("#wide_div").getLocation().getY(), 70, 85); // FF: 81, Chrome: 79

    zoom(1.1);
    assertBetween($("#wide_div").getLocation().getY(), 80, 100); // FF: 87, Chrome: 85, JenkinsFF: 91
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);

    zoom(2.0);
    assertBetween($("#wide_div").getLocation().getY(), 130, 160); // FF: 141, Chrome: 138
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);

    zoom(0.5);
    assertBetween($("#wide_div").getLocation().getY(), 50, 70); // FF: 51, Chrome: 50
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);
  }

  private static void assertBetween(int n, int lower, int upper) {
    Assertions.assertThat(n >= lower)
      .withFailMessage(n + " should be between " + lower + " and " + upper)
      .isTrue();
    Assertions.assertThat(n <= upper)
      .withFailMessage(n + " should be between " + lower + " and " + upper)
      .isTrue();
  }
}
