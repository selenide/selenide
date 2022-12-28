package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class AttributesMismatch extends UIAssertionError {
  public AttributesMismatch(String attribute, CollectionSource collection,
                            List<String> expectedValues, List<String> actualValues,
                            @Nullable String explanation, long timeoutMs) {
    super(
      "Attribute '"+attribute+"' values mismatch"  +
        lineSeparator() + "Actual: " + actualValues +
        lineSeparator() + "Expected: " + expectedValues +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
            expectedValues, actualValues);
    super.timeoutMs = timeoutMs;
  }
}
