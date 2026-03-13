package integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite(failIfNoTests = false)
@SelectClasses({
  ScreenshotsTest.class,
  ScreenshotTest.class,
  ScreenshotInIframeTest.class
})
public class ExistingScreenshotTestsWithFullSizePhotographer {
}
