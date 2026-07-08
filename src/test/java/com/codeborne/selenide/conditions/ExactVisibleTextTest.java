package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockSelect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExactVisibleTextTest {
  private final Driver driver = new DriverStub();

  @Test
  void to_string() {
    assertThat(new ExactVisibleText("Hello World")).hasToString("exact visible text \"Hello World\"");
  }

  @Test
  void negate_to_string() {
    assertThat(new ExactVisibleText("Hello World").negate()).hasToString("not exact visible text \"Hello World\"");
  }

  @Test
  void check_select_caseInsensitive() {
    ExactVisibleText condition = new ExactVisibleText("john malkovich", mockSelectedTextExtractor("John Malkovich The First"));
    SelenideElement select = mockSelect();
    assertThat(condition.check(driver, select).verdict()).isEqualTo(REJECT);
  }

  @Test
  void check_select_exactMatch() {
    ExactVisibleText condition = new ExactVisibleText("Hello World", mockSelectedTextExtractor("Hello World"));
    SelenideElement select = mockSelect();
    assertThat(condition.check(driver, select).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void check_select_partialMatchIsRejected() {
    ExactVisibleText condition = new ExactVisibleText("Hello", mockSelectedTextExtractor("Hello World"));
    SelenideElement select = mockSelect();

    assertThat(condition.check(driver, select))
      .usingRecursiveComparison()
      .ignoringFields("timestamp")
      .isEqualTo(new CheckResult(REJECT, "text=\"Hello World\""));
  }

  private GetSelectedOptionText mockSelectedTextExtractor(String selectedText) {
    GetSelectedOptionText command = mock();
    when(command.execute(any(), any())).thenReturn(selectedText);
    return command;
  }
}
