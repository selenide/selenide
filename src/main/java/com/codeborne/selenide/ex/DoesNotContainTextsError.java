package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class DoesNotContainTextsError extends UIAssertionError {

  public DoesNotContainTextsError(CollectionSource collection,
                                  List<String> expectedTexts, List<String> actualTexts, List<String> difference,
                                  @Nullable String explanation, long timeoutMs, @Nullable Throwable cause) {
    super(collection.driver(),
      "The collection with text elements: " + actualTexts +
        lineSeparator() + "should contain all of the following text elements: " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "but could not find these elements: " + difference +
        lineSeparator() + "Collection: " + collection.description(),
      expectedTexts,
      actualTexts,
      cause,
      timeoutMs);
  }
}
