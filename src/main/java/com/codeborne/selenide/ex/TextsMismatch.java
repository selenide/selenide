package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class TextsMismatch extends UIAssertionError {
  public TextsMismatch(String message, CollectionSource collection,
                       List<String> expectedTexts, List<String> actualTexts,
                       @Nullable String explanation, long timeoutMs,
                       @Nullable Throwable cause) {
    super(
      collection.driver(),
      message +
        lineSeparator() + "Actual (" + actualTexts.size() + "): " + actualTexts +
        lineSeparator() + "Expected (" + expectedTexts.size() + "): " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      expectedTexts, actualTexts,
      cause,
      timeoutMs);
  }
}
