package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class VisibleTextTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));

  @Test
  void to_string() {
    assertThat(new VisibleText("Hello World")).hasToString("visible text \"Hello World\"");
  }

  @Test
  void negate_to_string() {
    assertThat(new VisibleText("Hello World").negate()).hasToString("not visible text \"Hello World\"");
  }

  @Test
  void check_select_caseInsensitive() {
    VisibleText condition = new VisibleText("john malkovich", mockSelectedTextExtractor("John Malkovich The First"));
    SelenideElement select = mockSelect();
    assertThat(condition.check(driver, select).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void check_select() {
    VisibleText condition = new VisibleText("Hello World", mockSelectedTextExtractor("Hello from js underworld"));
    SelenideElement select = mockSelect();

    assertThat(condition.check(driver, select))
      .usingRecursiveComparison()
      .ignoringFields("timestamp")
      .isEqualTo(new CheckResult(REJECT, "text=\"Hello from js underworld\""));
  }

  private GetSelectedOptionText mockSelectedTextExtractor(String selectedText) {
    GetSelectedOptionText command = mock();
    when(command.execute(any(), any())).thenReturn(selectedText);
    return command;
  }
}
