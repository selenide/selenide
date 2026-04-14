package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VideoMergerTest {
  @Test
  @SuppressWarnings("PointlessArithmeticExpression")
  void frameRate() {
    Screenshot s1 = new Screenshot(0L, new ImageSource(new File("1.png")));
    Screenshot s2 = new Screenshot(500_000_000L, new ImageSource(new File("2.png")));
    Screenshot s3 = new Screenshot(1_000_000_000L, new ImageSource(new File("3.png")));
    Screenshot s4 = new Screenshot(2_000_000_000L, new ImageSource(new File("4.png")));

    assertThat(VideoMerger.frameRate(List.of(s1, s2, s3, s4))).isEqualTo(3 / 2.0f);
    assertThat(VideoMerger.frameRate(List.of(s1, s2, s3))).isEqualTo(2 / 1.0f);
    assertThat(VideoMerger.frameRate(List.of(s1, s3))).isEqualTo(1 / 1.0f);
    assertThat(VideoMerger.frameRate(List.of(s2, s4))).isEqualTo(1 / 1.5f);
    assertThat(VideoMerger.frameRate(List.of(s1))).isEqualTo(1);
  }

  @ParameterizedTest
  @ValueSource(ints =  {0, 2, 4, 6, 8, 10, 600, 800, 1024, 2048, 4096})
  void even_keepsEvenNumberUntouched(int height) {
    assertThat(VideoMerger.even(height)).isEqualTo(height);
  }

  @ParameterizedTest
  @ValueSource(ints =  {1, 3, 5, 7, 9, 11, 67, 111, 777, 1037})
  void even_ceilOddNumberToMakeEvenResult(int height) {
    assertThat(VideoMerger.even(height)).isEqualTo(height + 1);
  }
}
