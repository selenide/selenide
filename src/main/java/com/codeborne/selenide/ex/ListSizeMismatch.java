package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class ListSizeMismatch extends UIAssertionError {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  public ListSizeMismatch(String operator, int expectedSize,
                          @Nullable String explanation,
                          CollectionSource collection,
                          @Nullable List<WebElement> actualElements,
                          @Nullable Exception cause,
                          long timeoutMs) {
    super(
      collection.driver(),
      "List size mismatch: expected: " + operator + ' ' + expectedSize +
        (explanation == null ? "" : " (because " + explanation + ")") +
        ", actual: " + sizeOf(actualElements) +
        ", collection: " + collection.description() +
        lineSeparator() + "Elements: " + describe.fully(collection.driver(), actualElements),
      expectedSize,
      sizeOf(actualElements),
      cause,
      timeoutMs
    );
  }

  @CheckReturnValue
  private static int sizeOf(@Nullable Collection<?> collection) {
    return collection == null ? 0 : collection.size();
  }
}
