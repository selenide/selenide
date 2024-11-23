package integration.videorecorder.testng;

import com.codeborne.selenide.ex.ElementNotFound;
import org.selenide.videorecorder.testng.VideoRecorderListener;
import org.testng.ITestResult;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.ITestResult.SUCCESS;

public class MarkFailedTestsAsPassed extends VideoRecorderListener {
  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    if ("secondTest".equals(result.getName())) {
      assertThat(result.getThrowable()).isInstanceOf(ElementNotFound.class);
      assertThat(result.getThrowable().getMessage()).matches(Pattern.compile(".+Video: file:/.+\\.webm.*", DOTALL));
    }

    result.setStatus(SUCCESS);
    result.setThrowable(null);
  }
}
