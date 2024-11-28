package integration.videorecorder.testng;

import org.selenide.videorecorder.testng.VideoRecorderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.ThreadLocal.withInitial;
import static java.util.regex.Pattern.DOTALL;

public class VideoRecorderTester extends VideoRecorderListener {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderTester.class);
  static final Pattern RE_VIDEO_URL = Pattern.compile(".+Video: file:/.+\\.webm.*", DOTALL);

  private static final ThreadLocal<List<AssertionError>> threadErrors = withInitial(() -> new ArrayList<>(1));

  static List<AssertionError> assertionErrors() {
    return threadErrors.get();
  }

  @SuppressWarnings("ErrorNotRethrown")
  static void expectAssertionError(Runnable block) {
    try {
      block.run();
    }
    catch (AssertionError expected) {
      threadErrors.get().add(expected);
    }
  }

  @Override
  public void onTestStart(ITestResult result) {
    assertionErrors().clear();
    super.onTestStart(result);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    log.info("onTestSuccess {}", result);
    finish(result, !assertionErrors().isEmpty());
  }
}
