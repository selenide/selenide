package integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ZoomTest extends ITest {
  @Test
  void canZoomInAndOut() {
    openFile("page_with_big_divs.html");
    int initialX = $("#wide_div").getLocation().getX();
    assertBetween($("#wide_div").getLocation().getY(), 70, 85); // FF: 81, Chrome: 79

    driver().zoom(1.1);
    assertBetween($("#wide_div").getLocation().getY(), 80, 100); // FF: 87, Chrome: 85, JenkinsFF: 91
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);

    driver().zoom(2.0);
    assertBetween($("#wide_div").getLocation().getY(), 130, 160); // FF: 141, Chrome: 138
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);

    driver().zoom(0.5);
    assertBetween($("#wide_div").getLocation().getY(), 50, 70); // FF: 51, Chrome: 50
    assertThat($("#wide_div").getLocation().getX()).isEqualTo(initialX);
  }

  private static void assertBetween(int n, int lower, int upper) {
    assertThat(n >= lower)
      .withFailMessage(n + " should be between " + lower + " and " + upper)
      .isTrue();
    assertThat(n <= upper)
      .withFailMessage(n + " should be between " + lower + " and " + upper)
      .isTrue();
  }
}
