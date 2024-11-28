package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;

import static java.lang.System.lineSeparator;

public class MatcherError extends UIAssertionError {

  public MatcherError(@Nullable String explanation,
                      String expected, String actual,
                      CollectionSource collection,
                      @Nullable Exception cause,
                      long timeoutMs) {
    super(
      "Collection matcher error" +
        lineSeparator() + "Expected: " + expected +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description() +
        lineSeparator() + "Elements: " + actual,
      expected, "Elements: " + actual,
      cause,
      timeoutMs
    );
  }
}
