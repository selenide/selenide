package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.VideoMerger.framesCount;

class VideoMergerTest {
  @Test
  void framesCountBetweenTwoMoments() {
    assertThat(framesCount(24, 10_000_000_000L, 11_000_000_000L)).isEqualTo(24);
    assertThat(framesCount(12, 10_000_000_000L, 11_000_000_000L)).isEqualTo(12);
    assertThat(framesCount(6, 10_000_000_000L, 11_000_000_000L)).isEqualTo(6);
    assertThat(framesCount(24, 10_000_000_000L, 10_500_000_000L)).isEqualTo(12);
    assertThat(framesCount(12, 10_000_000_000L, 11_000_000_000L)).isEqualTo(12);
    assertThat(framesCount(48, 0, 2_000_000_000L)).isEqualTo(96);
  }
}
