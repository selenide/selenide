package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.VideoMerger.framesCount;

class VideoMergerTest {
  @Test
  void framesCountBetweenTwoMoments() {
    assertThat(framesCount(24, 10_000, 11_000)).isEqualTo(24);
    assertThat(framesCount(24, 10_000, 10_500)).isEqualTo(12);
    assertThat(framesCount(12, 10_000, 11_000)).isEqualTo(12);
    assertThat(framesCount(48, 0, 2_000)).isEqualTo(96);
    assertThat(framesCount(12, 1731761898633L, 1731761898728L)).isEqualTo(1);
  }
}
