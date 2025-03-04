package integration.videorecorder.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.ThreadLocal.withInitial;
import static java.util.regex.Pattern.DOTALL;

public class VideoRecorderTester extends VideoRecorderExtension {
  private static final ThreadLocal<List<AssertionError>> threadErrors = withInitial(() -> new ArrayList<>(1));
  static final Pattern RE_VIDEO_URL = Pattern.compile(".+Video: file:/.+\\.webm.*", DOTALL);

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
  public void afterTestExecution(ExtensionContext context) {
    boolean testFailed = !assertionErrors().isEmpty();
    afterTestExecution(context, testFailed);
  }
}
