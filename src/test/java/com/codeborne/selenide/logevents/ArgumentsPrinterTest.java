package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.logevents.ArgumentsPrinter.readableArguments;
import static org.assertj.core.api.Assertions.assertThat;

class ArgumentsPrinterTest {
  @Test
  @SuppressWarnings("RedundantCast")
  void printsReadableArgumentsValues() {
    assertThat(readableArguments((Object[]) null)).isEqualTo("");
    assertThat(readableArguments(111)).isEqualTo("111");
    assertThat(readableArguments(1, 2, 3)).isEqualTo("[1, 2, 3]");
    assertThat(readableArguments(1, new Integer[] {2, 3})).isEqualTo("[1, 2, 3]");
    assertThat(readableArguments(1, new int[] {2, 3})).isEqualTo("[1, 2, 3]");
    assertThat(readableArguments((Object[]) new String[]{"a"})).isEqualTo("a");
    assertThat(readableArguments((Object[]) new String[]{"a", "bb"})).isEqualTo("[a, bb]");
    assertThat(readableArguments((Object[]) new String[]{null})).isEqualTo("null");
    assertThat(readableArguments((Object[]) new String[]{null, "a", null})).isEqualTo("[null, a, null]");
    assertThat(readableArguments((Object) new int[]{1})).isEqualTo("1");
    assertThat(readableArguments((Object) new int[]{1, 2})).isEqualTo("[1, 2]");
  }

  @Test
  void printsDurationAmongArguments() {
    assertThat(readableArguments(Duration.ofMillis(900))).isEqualTo("900 ms.");
    assertThat(readableArguments(visible, Duration.ofSeconds(42))).isEqualTo("[visible, 42 s.]");
    assertThat(readableArguments(visible, Duration.ofMillis(8500))).isEqualTo("[visible, 8.500 s.]");
    assertThat(readableArguments(visible, Duration.ofMillis(900))).isEqualTo("[visible, 900 ms.]");
    assertThat(readableArguments(visible, Duration.ofNanos(0))).isEqualTo("[visible, 0 ms.]");
  }

  @Test
  void ignoresEmptyVararg() {
    assertThat(readableArguments("Option value")).isEqualTo("Option value");
    assertThat(readableArguments("Option value", new String[0])).isEqualTo("Option value");
    assertThat(readableArguments("Option value", new String[] {"Another option"})).isEqualTo("[Option value, Another option]");
    assertThat(readableArguments((Object[]) null)).isEqualTo("");
    assertThat(readableArguments((Object[]) new String[] {""})).isEqualTo("");
    assertThat(readableArguments((Object[]) new String[] {null})).isEqualTo("null");
  }

  @Test
  void joinVarargWithPreviousArgOfSameType() {
    assertThat(readableArguments("1st", new String[] {"2nd", "3rd"})).isEqualTo("[1st, 2nd, 3rd]");
    assertThat(readableArguments(1, new String[] {"2", "3"})).isEqualTo("[1, [2, 3]]");
    assertThat(readableArguments((Object[]) new String[] {"3"})).isEqualTo("3");
    assertThat(readableArguments((Object[]) new String[] {"2", "3"})).isEqualTo("[2, 3]");
  }

}
