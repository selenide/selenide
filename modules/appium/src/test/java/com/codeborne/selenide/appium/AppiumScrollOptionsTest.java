package com.codeborne.selenide.appium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.appium.ScrollDirection.UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class AppiumScrollOptionsTest {
  private final WebElement container = mock("recycler view");

  @Test
  void hasNoContainerByDefault() {
    assertThat(AppiumScrollOptions.down().getContainer()).isNull();
    assertThat(AppiumScrollOptions.up(10).getContainer()).isNull();
    assertThat(AppiumScrollOptions.with(DOWN, 0.1f, 0.6f).getContainer()).isNull();
  }

  @Test
  void inside_setsContainer_andKeepsOtherOptions() {
    AppiumScrollOptions options = AppiumScrollOptions.up(0.1f, 0.6f).inside(container);

    assertThat(options.getContainer()).isSameAs(container);
    assertThat(options.getScrollDirection()).isEqualTo(UP);
    assertThat(options.getMaxSwipeCount()).isEqualTo(30);
    assertThat(options.getTopPointHeightPercent()).isEqualTo(0.1f);
    assertThat(options.getBottomPointHeightPercent()).isEqualTo(0.6f);
  }

  @Test
  void inside_doesNotModifyOriginalOptions() {
    AppiumScrollOptions original = AppiumScrollOptions.down(5);

    AppiumScrollOptions withContainer = original.inside(container);

    assertThat(withContainer).isNotSameAs(original);
    assertThat(original.getContainer()).isNull();
  }

  @Test
  void toString_withoutContainer() {
    assertThat(AppiumScrollOptions.down(5))
      .hasToString("DOWN, max swipes: 5, top height: 0.25, bottom height: 0.5");
  }

  @Test
  void toString_withContainer() {
    assertThat(AppiumScrollOptions.down(5).inside(container))
      .hasToString("DOWN, max swipes: 5, top height: 0.25, bottom height: 0.5, inside: recycler view");
  }
}
