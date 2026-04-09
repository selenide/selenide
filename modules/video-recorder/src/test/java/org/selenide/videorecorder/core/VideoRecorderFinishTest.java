package org.selenide.videorecorder.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see <a href="https://github.com/selenide/selenide/issues/3278">#3278</a>
 */
class VideoRecorderFinishTest {

  @Test
  void finish_withNoScreenshots_returnsEmpty(@TempDir Path tempDir) {
    VideoConfiguration config = testConfig(tempDir);
    VideoRecorder recorder = new VideoRecorder(config);

    Optional<Path> result = recorder.finish();

    assertThat(result)
        .as("finish() should return empty when no screenshots were captured")
        .isEmpty();
  }

  @Test
  void finish_withNoScreenshots_doesNotLeavePathInRecordedVideos(@TempDir Path tempDir) {
    VideoConfiguration config = testConfig(tempDir);
    VideoRecorder recorder = new VideoRecorder(config);

    recorder.finish();

    assertThat(RecordedVideos.getRecordedVideo(Thread.currentThread().getId()))
        .as("RecordedVideos should not contain path when no video was created")
        .isEmpty();
  }

  private static VideoConfiguration testConfig(Path folder) {
    return new VideoConfiguration() {
      @Override
      public boolean videoEnabled() {
        return true;
      }

      @Override
      public String videoFolder() {
        return folder.toString();
      }

      @Override
      public RecordingMode mode() {
        return RecordingMode.ALL;
      }

      @Override
      public VideoSaveMode saveMode() {
        return VideoSaveMode.ALL;
      }

      @Override
      public int fps() {
        return 24;
      }

      @Override
      public int crf() {
        return 0;
      }

      @Override
      public long videoProcessingTimeout() {
        return 90;
      }

      @Override
      public boolean keepScreenshots() {
        return false;
      }
    };
  }
}
