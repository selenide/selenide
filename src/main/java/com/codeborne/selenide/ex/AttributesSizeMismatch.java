package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class AttributesSizeMismatch extends UIAssertionError {
  public AttributesSizeMismatch(Driver driver, String attribute, CollectionSource collection,
                                List<String> expectedValues, int actualSize,
                                @Nullable String explanation, long timeoutMs, @Nullable Exception cause) {
    super(driver,
      "Attribute '" + attribute + "' values size mismatch" +
        lineSeparator() + "Actual list size: " + actualSize +
        lineSeparator() + "Expected: " + expectedValues + ", List size: " + expectedValues.size() +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
        expectedValues.size(), actualSize, cause, timeoutMs
    );
  }
}
