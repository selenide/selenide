package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.assertj.core.api.Assertions.assertThat;

final class SimpleReportTest {
  private static final Pattern REGEX_LINE_SEPARATOR = Pattern.compile("\\n");

  @Test
  void reportShouldNotThrowNpe() {
    new SimpleReport().finish("test");
  }

  @Test
  void reportSample() throws IOException {
    String report = new SimpleReport().generateReport("userCanLogin", asList(
      new Log("webdriver", "create()", PASS),
      new Log("open", "google.com", PASS),
      new Log("#loginButton", "click(200, 100)", PASS),
      new Log(".user-name", "should have(text Степан)", FAIL)
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test1.txt"));
  }

  @Test
  void reportWithTooLongValue() throws IOException {
    String report = new SimpleReport().generateReport("userCanOpenVeryLongUrl", asList(
      new Log("webdriver", "create()", PASS),
      new Log("open", "https://some.com/authorization/authentication/modularization/internationalization", PASS),
      new Log("#loginButton", "click", FAIL)
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test2.txt"));
  }

  @Test
  void reportWithLongSelectors() throws IOException {
    String report = new SimpleReport().generateReport("userCanUseVeryLongSelectors", asList(
      new Log("open", "about:blank", PASS),
      new Log("#any-long-ugly-selector-should-be-entirely-visible", "click", PASS),
      new Log("close", "", FAIL)
    ));

    assertThat(report).isEqualTo(sample("/simple-report-test4.txt"));
  }

  @Test
  void emptyReport() throws IOException {
    String report = new SimpleReport().generateReport("userMightNotHaveDoneAnySteps", emptyList());

    assertThat(report).isEqualTo(sample("/simple-report-test3.txt"));
  }

  private String sample(String filename) throws IOException {
    return REGEX_LINE_SEPARATOR.matcher(resourceToString(filename, UTF_8)).replaceAll(lineSeparator());
  }

  private static class Log extends SelenideLog {
    Log(String element, String subject, EventStatus status) {
      super(element, subject);
      setStatus(status);
    }

    @Override
    public long getDuration() {
      return 100;
    }
  }
}
