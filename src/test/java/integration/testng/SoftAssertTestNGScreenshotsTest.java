package integration.testng;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.errormessages.Helper.assertScreenshot;

public class SoftAssertTestNGScreenshotsTest extends AbstractSoftAssertTestNGTest {

  private final TestLogListener testLogListener = new TestLogListener();

  @BeforeMethod
  public void addListener() {
    SelenideLogger.addListener("SoftAssertTestLogListener", testLogListener);
  }

  @Test()
  public void shouldTakeScreenshotForSoftAsserts() {
    $("h22").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    testLogListener.getEvents().map(LogEvent::getError).forEach(error -> {
      assertThat(error)
        .isInstanceOf(UIAssertionError.class);
      assertScreenshot((UIAssertionError) error);
    });
  }

  @AfterMethod
  public void tearDown() {
    SelenideLogger.removeListener("SoftAssertTestLogListener");
  }

  private class TestLogListener implements LogEventListener {

    private final List<LogEvent> events = new ArrayList<>();

    Stream<LogEvent> getEvents() {
      return events.stream();
    }

    @Override
    public void onEvent(LogEvent currentLog) {
      events.add(currentLog);
    }
  }
}
