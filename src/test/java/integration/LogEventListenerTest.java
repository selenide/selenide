package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static java.lang.String.format;

final class LogEventListenerTest extends BaseIntegrationTest {
  private final SelenideDriver driver = new SelenideDriver(new SelenideConfig().baseUrl(getBaseUrl()));
  private final List<String> beforeEvents = new ArrayList<>();
  private final List<String> afterEvents = new ArrayList<>();

  @AfterEach
  void tearDown() {
    driver.close();
  }

  @Test
  void canCatchBeforeAndAfterEvents() {
    SelenideLogger.addListener("log events collector", new SelenideListener());

    driver.open("/elements_disappear_on_click.html");

    SelenideElement removeMeButton = driver.$("#remove").shouldBe(visible);
    removeMeButton.click();
    removeMeButton.shouldNotBe(visible);

    SoftAssertions sa = new SoftAssertions();
    sa.assertThat(beforeEvents).hasSize(1);
    sa.assertThat(beforeEvents.get(0)).isEqualTo("before: $(#remove) click()");

    sa.assertThat(afterEvents).hasSize(4);

    sa.assertThat(afterEvents.get(0)).startsWith("after: $(open)");
    sa.assertThat(afterEvents.get(1)).isEqualTo("after: $(#remove) should be(visible)");
    sa.assertThat(afterEvents.get(2)).isEqualTo("after: $(#remove) click()");
    sa.assertThat(afterEvents.get(3)).isEqualTo("after: $(#remove) should not be(visible)");

    sa.assertAll();
  }

  @ParametersAreNonnullByDefault
  private class SelenideListener implements LogEventListener {
    @Override
    public void afterEvent(LogEvent logEvent) {
      afterEvents.add(format("after: $(%s) %s", logEvent.getElement(), logEvent.getSubject()));
    }

    @Override
    public void beforeEvent(LogEvent logEvent) {
      if (logEvent.getSubject().contains("click()")) {
        beforeEvents.add(format("before: $(%s) %s", logEvent.getElement(), logEvent.getSubject()));
      }
    }
  }
}
