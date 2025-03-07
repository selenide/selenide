package com.codeborne.selenide.ex;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Strings {
  public static String join(String first, String second) {
    return isEmpty(first) || isEmpty(second) ?
      first + second :
      first + lineSeparator() + second;
  }

  public static String join(String... texts) {
    return Stream.of(texts)
      .filter(s -> !s.isEmpty())
      .collect(joining(lineSeparator()));
  }
}
