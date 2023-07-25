package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(String operator,
                          int expectedSize,
                          int actualSize,
                          @Nullable String explanation,
                          CollectionSource collection,
                          @Nullable Exception cause,
                          long timeoutMs) {
    super(
      collection.driver(),
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
