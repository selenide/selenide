package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementSource;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;

/**
 * Almost the same as {@link Command}, but returns {@link SelenideElement} (non-nullable!).
 * Convenient to write fluent apis (that may be chained):
 *
 * <pre>
 *   {@code $("button")
 *    .execute(highlight())     // custom command
 *    .execute(pressEscape())   // another custom command
 *    .click();
 *    }
 * </pre>
 */
public abstract class FluentCommand implements Command<SelenideElement> {
  @Override
  @CanIgnoreReturnValue
  public final SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    execute(locator, args);
    return proxy;
  }

  protected abstract void execute(WebElementSource locator, Object @Nullable [] args);
}
