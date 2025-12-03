package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;

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
  }
}
