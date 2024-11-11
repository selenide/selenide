package integration;

import com.codeborne.selenide.logevents.EventsCollector;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.logevents.SimpleReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.SetValueOptions.withDate;
import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.assertj.core.api.Assertions.assertThat;

public class StepsTest extends IntegrationTest {
  private final EventsCollector events = new EventsCollector();

  @Test
  void canUseInnerSteps() {
    step("Edit CV", () -> {
      String previousBirthday = step("open CV page", () -> {
        openFile("page_with_date_input.html");
        return $("#birthday").val();
      });

      step("invalid birthday", () -> {
        $("#birthday").setValue(withDate(LocalDate.parse("2099-01-01")));
        $("#user-summary").shouldHave(text("User birthday: 2099-01-01"));
      });

      step("valid birthday", () -> {
        $("#birthday").setValue(withDate(LocalDate.parse(previousBirthday)));
        $("#user-summary").shouldHave(text("User birthday: " + previousBirthday));
      });
    });

    List<String> steps = stepsFromReport();

    assertThat(steps).containsExactly(
      "| Element              |",
      "| Edit CV              |",
      "|   open CV page       |",
      "|     open             |",
      "|     #birthday        |",
      "|   invalid birthday   |",
      "|     #birthday        |",
      "|     #user-summary    |",
      "|   valid birthday     |",
      "|     #birthday        |",
      "|     #user-summary    |"
    );
  }

  private List<String> stepsFromReport() {
    String report = SimpleReportTester.report(events.events());

    return report.lines()
      .filter(line -> line.startsWith("|"))
      .map(line -> line.replaceFirst("(\\|.+?\\|).*", "$1"))
      .toList();
  }

  private static class SimpleReportTester extends SimpleReport {
    static String report(List<LogEvent> events) {
      return new SimpleReportTester().generateReport("", events);
    }
  }

  @BeforeEach
  void setUp() {
    open();
    SelenideLogger.addListener("StepsTest.listener", events);
  }

  @AfterEach
  void tearDown() {
    SelenideLogger.removeListener("StepsTest.listener");
  }
}
