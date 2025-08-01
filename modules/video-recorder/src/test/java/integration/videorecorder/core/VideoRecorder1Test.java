package integration.videorecorder.core;

import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.lang.Thread.currentThread;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.RecordedVideos.getRecordedVideo;

@ExtendWith({TextReportExtension.class, TestSetup.class})
public class VideoRecorder1Test {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder1Test.class);

  private final VideoRecorder videoRecorder = new VideoRecorder();
  private final String threadName = currentThread().getName();

  @BeforeEach
  public void beforeEach() {
    currentThread().setName(threadName + " #" + videoRecorder.videoId);
    Runtime runtime = Runtime.getRuntime();
    log.info("before first test, cores: {}, freeMem: {}, totalMem: {}, maxMem: {}", runtime.availableProcessors(),
      runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory());
    videoRecorder.start();
  }

  @RepeatedTest(20)
  public void firstTest() throws InterruptedException {
    log.info("[first] open()");
    open(config().browserPosition("50x5").browserSize("800x500"));
    ThreadDumper threadDumper = new ThreadDumper(videoRecorder.videoId);
    threadDumper.start();

    for (int i = 0; i < 3; i++) {
      log.info("[first] #{} open('search.html')", i);
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      log.info("[first] #{} type(#1 ... #111)", i);
      $("[name=q]").type(text("#1 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #111")
        .withDelay(ofMillis(5)));
      log.info("[first] #{} typeed(#1 ... #111)", i);
    }
    log.info("[first] end()");
    threadDumper.stop();
  }

  @AfterEach
  public void checkVideo() {
    log.info("finishing first test");
    videoRecorder.finish();
    log.info("finished first test");
    currentThread().setName(threadName);

    Path videoFile = getRecordedVideo(currentThread().getId())
      .orElseThrow(() -> new AssertionError("video file not found in thread " + currentThread().getId()));
    assertThat(videoFile).hasExtension("mp4");
    assertThat(videoFile.toFile().length())
      .as(() -> "Video file %s for thread %s".formatted(videoFile, currentThread().getId()))
      .isGreaterThan(0);
  }

  @AfterEach
  void closeBrowser() {
    log.info("closing first browser");
    closeWebDriver();
    log.info("closed first browser");
  }
}
