package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(String operator, int expectedSize,
                          @Nullable String explanation,
                          CollectionSource collection,
                          @Nullable List<WebElement> actualElements,
                          @Nullable Exception lastError,
                          long timeoutMs) {
    super(
      collection.driver(),
      "List size mismatch: expected: " + operator + ' ' + expectedSize +
        (explanation == null ? "" : " (because " + explanation + ")") +
        ", actual: " + sizeOf(actualElements) +
        ", collection: " + collection.description() +
        lineSeparator() + "Elements: " + elementsToString(collection.driver(), actualElements),
      expectedSize,
      sizeOf(actualElements),
      lastError,
      timeoutMs
    );
  }

  @CheckReturnValue
  private static int sizeOf(@Nullable Collection<?> collection) {
    return collection == null ? 0 : collection.size();
  }
}
