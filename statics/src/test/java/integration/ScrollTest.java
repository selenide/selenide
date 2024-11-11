package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

final class ScrollTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ScrollTest.class);
  private static final int initialRabbitTop = 1000;
  private static final int initialRabbitLeft = 1200;
  private static final int rabbitHeight = 100;
  private static final int TOLERANCE_HORIZONTAL = 70;
  private static final int TOLERANCE_VERTICAL = 20;
  private int windowHeight;
  private int windowWidth;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_big_divs.html");

    Location initial = rabbitPosition();
    windowHeight = windowHeight();
    windowWidth = windowWidth();
    log.info("Initial rabbit position: {}, windowWidth: {}, windowHeight: {}", initial, windowWidth, windowHeight);
    assertPosition(initial, initialRabbitTop, initialRabbitLeft);
  }

  @Test
  void scrollTo() {
    $("#rabbit").scrollTo();

    assertTopPosition(rabbitPosition(), 0);
  }

  @Test
  void scrollIntoCenter() {
    $("#rabbit").scrollIntoCenter();

    assertTopPosition(rabbitPosition(), windowHeight / 2 - rabbitHeight / 2);
  }

  @Test
  void scrollIntoView_alignToTop() {
    $("#rabbit").scrollIntoView(true);

    assertTopPosition(rabbitPosition(), 0);
  }

  @Test
  void scrollIntoView_alignToBottom() {
    $("#rabbit").scrollIntoView(false);

    assertTopPosition(rabbitPosition(), windowHeight() - rabbitHeight);
  }

  @Test
  void scrollIntoView_options() {
    $("#rabbit").scrollIntoView("{behavior: \"instant\", inline: \"center\"}");

    assertPosition(rabbitPosition(), 0, windowWidth / 2);
  }

  @Test
  void scrollIntoView_horizontal() {
    $("#rabbit").scrollIntoView("""
      {behavior: "instant", block: "start", inline: "start"}
      """);

    assertPosition(rabbitPosition(), 0, 0);
  }

  private void assertPosition(Location position, int expectedTop, int expectedLeft) {
    assertTopPosition(position, expectedTop);
    assertThat(position.left).as("Left position").isBetween(expectedLeft - TOLERANCE_HORIZONTAL, expectedLeft + TOLERANCE_HORIZONTAL);
  }

  private void assertTopPosition(Location position, int expectedTop) {
    assertThat(position.top).as("Top position").isBetween(expectedTop - TOLERANCE_VERTICAL, expectedTop + TOLERANCE_VERTICAL);
  }

  private int windowHeight() {
    return read("return window.innerHeight");
  }

  private int windowWidth() {
    return read("return window.innerWidth");
  }

  private Location rabbitPosition() {
    List<Number> array = executeJavaScript("""
        const rect = document.querySelector('#rabbit').getBoundingClientRect();
        return [rect.left, rect.top]
      """);
    return new Location(array.get(0).intValue(), array.get(1).intValue());
  }

  private record Location(int left, int top) {
  }

  private int read(String js) {
    return ((Number) requireNonNull(executeJavaScript(js))).intValue();
  }
}
