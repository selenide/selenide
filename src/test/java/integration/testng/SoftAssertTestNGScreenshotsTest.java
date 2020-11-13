package integration.testng;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;

public class SoftAssertTestNGScreenshotsTest extends AbstractSoftAssertTestNGTest {

  private final TestLogListener testLogListener = new TestLogListener();

  @BeforeMethod
  public void addListener() {
    SelenideLogger.addListener("SoftAssertTestLogListener", testLogListener);
  }

  @Test
  public void shouldTakeScreenshotForSoftAsserts() {
    $("h22").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    testLogListener.getEvents().map(LogEvent::getError).forEach(error -> {
      assertThat(error).isInstanceOf(UIAssertionError.class);
      assertScreenshot((UIAssertionError) error);
    });
  }

  @AfterMethod
  public void tearDown() {
    SelenideLogger.removeListener("SoftAssertTestLogListener");
  }

  @ParametersAreNonnullByDefault
  private static class TestLogListener implements LogEventListener {

    private final List<LogEvent> events = new ArrayList<>();

    Stream<LogEvent> getEvents() {
      return events.stream();
    }

    @Override
    public void afterEvent(LogEvent currentLog) {
      events.add(currentLog);
    }

    @Override
    public void beforeEvent(LogEvent currentLog) {
      //ignore
    }
  }

  private void assertScreenshot(UIAssertionError expected) {
    assertThat(expected.getScreenshot().getImage())
      .contains(driver.config().reportsFolder());
  }
}
