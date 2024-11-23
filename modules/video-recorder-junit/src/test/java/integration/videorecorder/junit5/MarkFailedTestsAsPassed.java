package integration.videorecorder.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

public class MarkFailedTestsAsPassed extends VideoRecorderExtension {
  @Override
  public void afterTestExecution(ExtensionContext context) {
    boolean testFailed = "secondTest".equals(context.getRequiredTestMethod().getName());
    afterTestExecution(context, testFailed);
  }
}
