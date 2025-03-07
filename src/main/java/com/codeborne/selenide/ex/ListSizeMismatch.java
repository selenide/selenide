package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;

public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(String operator,
                          int expectedSize,
                          int actualSize,
                          @Nullable String explanation,
                          CollectionSource collection,
                          @Nullable Exception cause,
                          long timeoutMs) {
    super(
      "List size mismatch: expected: " + operator + ' ' + expectedSize +
        (explanation == null ? "" : " (because " + explanation + ")") +
        ", actual: " + actualSize +
        ", collection: " + collection.description(),
      expectedSize,
      actualSize,
      cause,
      timeoutMs
    );
  }
}
