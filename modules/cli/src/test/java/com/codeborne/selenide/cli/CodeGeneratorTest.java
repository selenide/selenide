package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGeneratorTest {
  private static final String DOLLAR = "com.codeborne.selenide.Selenide.$";
  private static final String VISIBLE = "com.codeborne.selenide.Condition.visible";

  private final CodeGenerator generator = new CodeGenerator();

  @Test
  void snippetEmitsSortedImportsThenStatements() {
    List<RecordedStatement> statements = List.of(
      RecordedStatement.of("$(\"#x\").click();", DOLLAR),
      RecordedStatement.of("$(\"#x\").shouldBe(visible);", DOLLAR, VISIBLE));

    assertThat(generator.snippet(statements)).isEqualTo("""
      import static com.codeborne.selenide.Condition.visible;
      import static com.codeborne.selenide.Selenide.$;

      $("#x").click();
      $("#x").shouldBe(visible);
      """);
  }

  @Test
  void snippetWithoutStatementsIsEmpty() {
    assertThat(generator.snippet(List.of())).isEmpty();
  }

  @Test
  void testClassWrapsStatementsInJunitMethod() {
    List<RecordedStatement> statements = List.of(RecordedStatement.of("$(\"#x\").click();", DOLLAR));

    assertThat(generator.testClass("LoginTest", statements)).isEqualTo("""
      import org.junit.jupiter.api.Test;

      import static com.codeborne.selenide.Selenide.$;

      public class LoginTest {
        @Test
        void test() {
          $("#x").click();
        }
      }
      """);
  }
}
