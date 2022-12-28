package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class AttributesSizeMismatch extends UIAssertionError {
  public AttributesSizeMismatch(String attribute, CollectionSource collection,
                                List<String> expectedValues, List<String> actualValues,
                                @Nullable String explanation, long timeoutMs) {
    super(
      "Attribute '"+attribute+"' values size mismatch"  +
        lineSeparator() + "Actual: " + actualValues + ", List size: " + actualValues.size() +
        lineSeparator() + "Expected: " + expectedValues + ", List size: " + expectedValues.size() +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      expectedValues, actualValues
    );
    super.timeoutMs = timeoutMs;
  }
}
