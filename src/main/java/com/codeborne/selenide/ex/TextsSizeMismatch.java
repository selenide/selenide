package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class TextsSizeMismatch extends UIAssertionError {
  public TextsSizeMismatch(WebElementsCollection collection, List<String> actualTexts,
                           List<String> expectedTexts, @Nullable String explanation, long timeoutMs) {
    super(collection.driver(),
      "Texts size mismatch" +
        lineSeparator() + "Actual: " + actualTexts + ", List size: " + actualTexts.size() +
        lineSeparator() + "Expected: " + expectedTexts + ", List size: " + expectedTexts.size() +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description()
    );
    super.timeoutMs = timeoutMs;
  }
}
