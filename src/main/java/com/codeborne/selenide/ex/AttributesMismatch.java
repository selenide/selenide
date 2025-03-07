package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static java.lang.System.lineSeparator;

public class AttributesMismatch extends UIAssertionError {
  public AttributesMismatch(String message, CollectionSource collection,
                            List<String> expectedValues, List<String> actualValues,
                            @Nullable String explanation, long timeoutMs, @Nullable Exception cause) {
    super(message +
        lineSeparator() + "Actual (" + actualValues.size() + "): " + actualValues +
        lineSeparator() + "Expected (" + expectedValues.size() + "): " + expectedValues +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      expectedValues, actualValues, cause, timeoutMs);
  }
}
