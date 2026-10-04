package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArgsTest {
  @Test
  void commandIsLowercased() {
    assertThat(Args.ofLine("Click #submit").command()).isEqualTo("click");
    assertThat(Args.ofTokens(List.of("Click", "#submit")).command()).isEqualTo("click");
  }

  @Test
  void selectorIsSecondToken() {
    assertThat(Args.ofLine("click #submit").selector()).isEqualTo("#submit");
  }

  @Test
  void valueJoinsRemainingTokens() {
    assertThat(Args.ofLine("setValue #q hello world").value()).isEqualTo("hello world");
    assertThat(Args.ofTokens(List.of("setValue", "#q", "hello world")).value()).isEqualTo("hello world");
  }

  @Test
  void doubleQuotesGroupValueWithSpaces() {
    assertThat(Args.ofLine("setValue #q \"hello world\"").value()).isEqualTo("hello world");
  }

  @Test
  void doubleQuotesGroupSelectorWithSpaces() {
    assertThat(Args.ofLine("click \"input[name='a b']\"").selector()).isEqualTo("input[name='a b']");
  }

  @Test
  void tokensFromShellArgvNeedNoQuoting() {
    // the shell already split the argv, so a value with spaces arrives as a single token
    assertThat(Args.ofTokens(List.of("click", "input[name='a b']")).selector()).isEqualTo("input[name='a b']");
  }

  @Test
  void emptyLineHasEmptyCommand() {
    assertThat(Args.ofLine("   ").command()).isEmpty();
  }

  @Test
  void countsTokens() {
    assertThat(Args.ofLine("a b c").count()).isEqualTo(3);
  }

  @Test
  void missingSelectorThrows() {
    assertThatThrownBy(() -> Args.ofLine("click").selector()).isInstanceOf(CommandException.class);
  }

  @Test
  void missingValueThrows() {
    assertThatThrownBy(() -> Args.ofLine("setValue #q").value()).isInstanceOf(CommandException.class);
  }
}
