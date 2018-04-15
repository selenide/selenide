package integration.testng;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.errormessages.Helper.assertScreenshot;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

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
      assertThat(error, instanceOf(UIAssertionError.class));
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
