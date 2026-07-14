package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CommandInterpreterTest {
  private final CommandInterpreter interpreter = new CommandInterpreter(mock(SelenideDriver.class));

  private String code(String line) {
    return interpreter.interpret(line).statement().code();
  }

  @Test
  void click() {
    assertThat(code("click #submit")).isEqualTo("$(\"#submit\").click();");
    assertThat(interpreter.interpret("click #submit").statement().staticImports())
      .containsExactly("com.codeborne.selenide.Selenide.$");
  }

  @Test
  void setValueEscapesAndQuotesText() {
    assertThat(code("setValue #email foo@bar.com")).isEqualTo("$(\"#email\").setValue(\"foo@bar.com\");");
    assertThat(code("setValue #q \"hello world\"")).isEqualTo("$(\"#q\").setValue(\"hello world\");");
  }

  @Test
  void typeIsAliasOfSetValue() {
    assertThat(code("type #q hi")).isEqualTo("$(\"#q\").setValue(\"hi\");");
  }

  @Test
  void clearAndPressAndHover() {
    assertThat(code("clear #q")).isEqualTo("$(\"#q\").clear();");
    assertThat(code("pressEnter #q")).isEqualTo("$(\"#q\").pressEnter();");
    assertThat(code("hover #menu")).isEqualTo("$(\"#menu\").hover();");
  }

  @Test
  void checkboxCommands() {
    assertThat(code("check #agree")).isEqualTo("$(\"#agree\").setSelected(true);");
    assertThat(code("uncheck #agree")).isEqualTo("$(\"#agree\").setSelected(false);");
    assertThat(code("setSelected #agree true")).isEqualTo("$(\"#agree\").setSelected(true);");
  }

  @Test
  void selectOptionAndRadio() {
    assertThat(code("selectOption #country Estonia")).isEqualTo("$(\"#country\").selectOption(\"Estonia\");");
    assertThat(code("selectRadio #gender male")).isEqualTo("$(\"#gender\").selectRadio(\"male\");");
  }

  @Test
  void byTextSelector() {
    assertThat(code("click text=Sign In")).isEqualTo("$(byText(\"Sign In\")).click();");
    assertThat(interpreter.interpret("click text=Sign In").statement().staticImports())
      .containsExactlyInAnyOrder("com.codeborne.selenide.Selenide.$", "com.codeborne.selenide.Selectors.byText");
  }

  @Test
  void xpathSelectors() {
    assertThat(code("click xpath=//a[@id='x']")).isEqualTo("$(byXpath(\"//a[@id='x']\")).click();");
    assertThat(code("click //button")).isEqualTo("$(byXpath(\"//button\")).click();");
  }

  @Test
  void shouldStateConditions() {
    assertThat(code("should #msg visible")).isEqualTo("$(\"#msg\").shouldBe(visible);");
    assertThat(code("should #msg exist")).isEqualTo("$(\"#msg\").should(exist);");
    assertThat(code("should #msg disappear")).isEqualTo("$(\"#msg\").should(disappear);");
  }

  @Test
  void shouldValueConditions() {
    assertThat(code("should #msg text Welcome home")).isEqualTo("$(\"#msg\").shouldHave(text(\"Welcome home\"));");
    assertThat(code("should #inp value 42")).isEqualTo("$(\"#inp\").shouldHave(value(\"42\"));");
    assertThat(interpreter.interpret("should #msg text Hi").statement().staticImports())
      .containsExactlyInAnyOrder("com.codeborne.selenide.Selenide.$", "com.codeborne.selenide.Condition.text");
  }

  @Test
  void shouldAttribute() {
    assertThat(code("should #a attribute href /home")).isEqualTo("$(\"#a\").shouldHave(attribute(\"href\", \"/home\"));");
    assertThat(code("should #a attribute data-x")).isEqualTo("$(\"#a\").shouldHave(attribute(\"data-x\"));");
  }

  @Test
  void navigation() {
    assertThat(code("open https://selenide.org")).isEqualTo("open(\"https://selenide.org\");");
    assertThat(code("back")).isEqualTo("back();");
    assertThat(code("forward")).isEqualTo("forward();");
    assertThat(code("refresh")).isEqualTo("refresh();");
  }

  @Test
  void screenshot() {
    assertThat(code("screenshot")).isEqualTo("screenshot(\"screenshot\");");
    assertThat(code("screenshot home")).isEqualTo("screenshot(\"home\");");
  }

  @Test
  void unknownCommandThrows() {
    assertThatThrownBy(() -> interpreter.interpret("frobnicate #x"))
      .isInstanceOf(CommandException.class)
      .hasMessageContaining("Unknown command");
  }

  @Test
  void missingSelectorThrows() {
    assertThatThrownBy(() -> interpreter.interpret("click")).isInstanceOf(CommandException.class);
  }

  @Test
  void invalidBooleanThrows() {
    assertThatThrownBy(() -> interpreter.interpret("setSelected #c maybe")).isInstanceOf(CommandException.class);
  }

  @Test
  void unknownConditionThrows() {
    assertThatThrownBy(() -> interpreter.interpret("should #m sparkling")).isInstanceOf(CommandException.class);
  }
}
