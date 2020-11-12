package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class ElementWithTextNotFound extends UIAssertionError {

  public ElementWithTextNotFound(WebElementsCollection collection, List<String> actualTexts,
                                 List<String> expectedTexts, @Nullable String explanation,
                                 long timeoutMs, @Nullable Throwable lastError) {
    super(collection.driver(),
      "Element with text not found" +
        lineSeparator() + "Actual: " + actualTexts +
        lineSeparator() + "Expected: " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
      lastError);
    super.timeoutMs = timeoutMs;
  }
}
