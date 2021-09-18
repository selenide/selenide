package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class MatcherError extends UIAssertionError {

  public MatcherError(String matcher,
                      String predicateDescription,
                      @Nullable String explanation,
                      CollectionSource collection,
                      List<WebElement> actualElements,
                      @Nullable Exception lastError,
                      long timeoutMs) {
    super(collection.driver(),
      "Collection matcher error" +
        lineSeparator() + "Expected: " + matcher + " of elements to match [" + predicateDescription + "] predicate" +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description() +
        lineSeparator() + "Elements: " + elementsToString(collection.driver(), actualElements), lastError
    );
    super.timeoutMs = timeoutMs;
  }

}
