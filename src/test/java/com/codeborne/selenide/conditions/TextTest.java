package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class TextTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));

  @Test
  void apply_for_textInput() {
    assertThat(new Text("Hello World").check(driver, mockElement("Hello World")).verdict()).isEqualTo(ACCEPT);
    assertThat(new Text("Hello World").check(driver, mockElement("Hello")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void apply_matchTextPartially() {
    assertThat(new Text("Hello").check(driver, mockElement("Hello World")).verdict()).isEqualTo(ACCEPT);
    assertThat(new Text("World").check(driver, mockElement("Hello World")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void check_select() {
    Text condition = new Text("Hello World");
    SelenideElement selectWithoutSpace = mockSelect(option("Hello", true), option("World", true));
    SelenideElement selectWithSpace = mockSelect(option("Hello", true), option(" World", true));

    assertThat(condition.check(driver, selectWithoutSpace))
      .usingRecursiveComparison()
      .ignoringFields("timestamp")
      .isEqualTo(new CheckResult(REJECT, "text=\"HelloWorld\""));
    assertThat(condition.check(driver, selectWithSpace))
      .usingRecursiveComparison()
      .ignoringFields("timestamp")
      .isEqualTo(new CheckResult(ACCEPT, "text=\"Hello World\""));
  }

  @Test
  void to_string() {
    assertThat(new Text("Hello World")).hasToString("text \"Hello World\"");
  }

  @Test
  void negate_to_string() {
    assertThat(new Text("Hello World").negate()).hasToString("not text \"Hello World\"");
  }

  @Test
  void apply_for_textInput_caseInsensitive() {
    WebElement element = mockElement("John Malkovich The First");
    assertThat(new Text("john malkovich").check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void apply_for_select_caseInsensitive() {
    WebElement element = mockSelect(
      option("John", true),
      option(" Malkovich", true),
      option(" The First", true)
    );
    assertThat(new Text("john malkovich").check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void apply_for_textInput_ignoresWhitespaces() {
    assertThat(new Text("john the malkovich")
      .check(driver, mockElement("John  the\n Malkovich")).verdict())
      .isEqualTo(ACCEPT);

    assertThat(new Text("This is nonbreakable space")
      .check(driver, mockElement("This is nonbreakable\u00a0space")).verdict())
      .isEqualTo(ACCEPT);
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    Text condition = new Text("Hello");
    WebElement element = mockElement("Hello World");
    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.actualValue()).isEqualTo("text=\"Hello World\"");
    verify(element).getTagName();
    verify(element).getText();
    verifyNoMoreInteractions(element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterSelectMatching() {
    Text condition = new Text("Hello");
    WebElement element = mockSelect(option("Hello", true), option(" World", true));
    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.actualValue()).isEqualTo("text=\"Hello World\"");
    // One time in Text condition, second in selenium Select
    verify(element, times(2)).getTagName();
  }
}
