package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;
import static java.lang.System.lineSeparator;

public class MatcherError extends UIAssertionError {

  public MatcherError(String matcher, String predicateDescription, String explanation,
                      WebElementsCollection collection, List<WebElement> actualElements, Exception lastError, long timeoutMs) {
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
