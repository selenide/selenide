package com.codeborne.selenide.appium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.LEFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class AppiumSwipeOptionsTest {
  private final WebElement container = mock("carousel");

  @Test
  void hasNoContainerByDefault() {
    assertThat(AppiumSwipeOptions.right().getContainer()).isNull();
    assertThat(AppiumSwipeOptions.left(10).getContainer()).isNull();
    assertThat(AppiumSwipeOptions.with(LEFT, 3).getContainer()).isNull();
  }

  @Test
  void inside_setsContainer_andKeepsOtherOptions() {
    AppiumSwipeOptions options = AppiumSwipeOptions.left(7).inside(container);

    assertThat(options.getContainer()).isSameAs(container);
    assertThat(options.getAppiumSwipeDirection()).isEqualTo(LEFT);
    assertThat(options.getMaxSwipeCounts()).isEqualTo(7);
  }

  @Test
  void inside_doesNotModifyOriginalOptions() {
    AppiumSwipeOptions original = AppiumSwipeOptions.right();

    AppiumSwipeOptions withContainer = original.inside(container);

    assertThat(withContainer).isNotSameAs(original);
    assertThat(original.getContainer()).isNull();
  }

  @Test
  void toString_withoutContainer() {
    assertThat(AppiumSwipeOptions.right(3)).hasToString("RIGHT, max swipes: 3");
  }

  @Test
  void toString_withContainer() {
    assertThat(AppiumSwipeOptions.right(3).inside(container)).hasToString("RIGHT, max swipes: 3, inside: carousel");
  }
}
