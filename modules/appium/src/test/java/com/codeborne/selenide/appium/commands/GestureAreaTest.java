package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.LEFT;
import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.appium.ScrollDirection.UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GestureAreaTest {
  private static final Dimension SCREEN = new Dimension(1000, 2000);

  private final WebDriver driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);

  @Test
  void wholeScreen_ifNoContainerGiven() {
    when(driver.manage().window().getSize()).thenReturn(SCREEN);

    assertThat(GestureArea.of(driver, null).rectangle()).isEqualTo(rect(0, 0, 1000, 2000));
  }

  @Test
  void containerBounds_ifContainerGiven() {
    when(driver.manage().window().getSize()).thenReturn(SCREEN);
    WebElement container = mock();
    when(container.getRect()).thenReturn(rect(100, 300, 800, 400));

    assertThat(GestureArea.of(driver, container).rectangle()).isEqualTo(rect(100, 300, 800, 400));
  }

  @Test
  void visiblePart_cutsOffPartsOfContainerOutsideScreen() {
    assertThat(GestureArea.visiblePart(rect(-50, 1500, 1200, 900), SCREEN).rectangle())
      .isEqualTo(rect(0, 1500, 1000, 500));
    assertThat(GestureArea.visiblePart(rect(200, -100, 300, 400), SCREEN).rectangle())
      .isEqualTo(rect(200, 0, 300, 300));
  }

  @Test
  void visiblePart_isEmpty_ifContainerIsFullyOutsideScreen() {
    GestureArea belowScreen = GestureArea.visiblePart(rect(0, 2100, 1000, 500), SCREEN);
    assertThat(belowScreen.rectangle()).isEqualTo(rect(0, 2000, 1000, 0));
    assertThat(belowScreen.isEmpty()).isTrue();

    GestureArea rightOfScreen = GestureArea.visiblePart(rect(1200, 100, 300, 500), SCREEN);
    assertThat(rightOfScreen.rectangle()).isEqualTo(rect(1000, 100, 0, 500));
    assertThat(rightOfScreen.isEmpty()).isTrue();
  }

  @Test
  void failsWithClearMessage_ifContainerIsFullyOutsideScreen() {
    when(driver.manage().window().getSize()).thenReturn(SCREEN);
    WebElement container = mock("carousel");
    when(container.getRect()).thenReturn(rect(0, 2100, 1000, 500));

    assertThatThrownBy(() -> GestureArea.of(driver, container))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Cannot perform gesture inside carousel: it's outside of the screen");
  }

  @Test
  void scrollDown_movesFingerFromBottomPointToTopPoint() {
    GestureArea area = new GestureArea(rect(100, 300, 800, 400));

    assertCoordinates(area.scrollCoordinates(DOWN, 0.25f, 0.5f), 500, 500, 500, 400);
  }

  @Test
  void scrollUp_movesFingerFromTopPointToBottomPoint() {
    GestureArea area = new GestureArea(rect(100, 300, 800, 400));

    assertCoordinates(area.scrollCoordinates(UP, 0.2f, 0.7f), 500, 380, 500, 580);
  }

  @Test
  void swipeRight_movesFingerFromRightToLeft() {
    GestureArea area = new GestureArea(rect(100, 300, 800, 400));

    assertCoordinates(area.swipeCoordinates(RIGHT), 700, 500, 300, 500);
  }

  @Test
  void swipeLeft_movesFingerFromLeftToRight() {
    GestureArea area = new GestureArea(rect(100, 300, 800, 400));

    assertCoordinates(area.swipeCoordinates(LEFT), 300, 500, 700, 500);
  }

  private static Rectangle rect(int x, int y, int width, int height) {
    return new Rectangle(new Point(x, y), new Dimension(width, height));
  }

  private static void assertCoordinates(AppiumScrollCoordinates coordinates, int startX, int startY, int endX, int endY) {
    assertThat(coordinates.getStartX()).as("startX").isEqualTo(startX);
    assertThat(coordinates.getStartY()).as("startY").isEqualTo(startY);
    assertThat(coordinates.getEndX()).as("endX").isEqualTo(endX);
    assertThat(coordinates.getEndY()).as("endY").isEqualTo(endY);
  }
}
