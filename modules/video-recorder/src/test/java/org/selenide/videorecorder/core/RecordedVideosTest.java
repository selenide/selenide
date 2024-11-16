package org.selenide.videorecorder.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static java.lang.System.nanoTime;
import static org.assertj.core.api.Assertions.assertThat;

class RecordedVideosTest {
  private final long threadId = nanoTime();

  @BeforeEach
  void setUp() {
    RecordedVideos.clear();
  }

  @Test
  void returnsLastVideo() {
    RecordedVideos.add(threadId, Path.of("tere.avi"));
    assertThat(RecordedVideos.getRecordedVideo(threadId)).contains(Path.of("tere.avi"));
  }

  @Test
  void returnsEmptyOption_ifNoVideo() {
    assertThat(RecordedVideos.getRecordedVideo(threadId)).isEmpty();
  }

  @Test
  void returnsEmptyOption_ifLastVideoWasNotRecorded() {
    RecordedVideos.add(threadId, Path.of("."));
    RecordedVideos.remove(threadId);
    assertThat(RecordedVideos.getRecordedVideo(threadId)).isEmpty();
  }
}
