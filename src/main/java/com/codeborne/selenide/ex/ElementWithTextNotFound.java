package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class ElementWithTextNotFound extends UIAssertionError {

  public ElementWithTextNotFound(CollectionSource collection,
                                 List<String> expectedTexts, @Nullable List<String> actualTexts,
                                 @Nullable String explanation,
                                 long timeoutMs, @Nullable Throwable cause) {
    super(
      collection.driver(),
      "Element with text not found" +
        lineSeparator() + "Actual: " + actualTexts +
        lineSeparator() + "Expected: " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      expectedTexts,
      actualTexts,
      cause,
      timeoutMs);
  }
}
