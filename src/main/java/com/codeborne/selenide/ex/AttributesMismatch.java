package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class AttributesMismatch extends UIAssertionError {
  public AttributesMismatch(Driver driver, String message, CollectionSource collection,
                            List<String> expectedValues, List<String> actualValues,
                            @Nullable String explanation, long timeoutMs, @Nullable Exception cause) {
    super(driver, message +
        lineSeparator() + "Actual (" + actualValues.size() + "): " + actualValues +
        lineSeparator() + "Expected (" + expectedValues.size() + "): " + expectedValues +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      expectedValues, actualValues, cause, timeoutMs);
  }
}
