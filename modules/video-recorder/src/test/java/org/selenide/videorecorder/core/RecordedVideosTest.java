package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RecordedVideosTest {
  private final RecordedVideos recordedVideos = new RecordedVideos();

  @Test
  void returnsLastVideo() {
    recordedVideos.add(Optional.of(Path.of("tere.avi")));
    assertThat(recordedVideos.getRecordedVideo()).contains(Path.of("tere.avi"));
  }

  @Test
  void returnsEmptyOption_ifNoVideo() {
    assertThat(recordedVideos.getRecordedVideo()).isEmpty();
  }

  @Test
  void returnsEmptyOption_ifLastVideoWasNotRecorded() {
    recordedVideos.add(Optional.of(Path.of(".")));
    recordedVideos.add(Optional.empty());
    assertThat(recordedVideos.getRecordedVideo()).isEmpty();
  }
}
