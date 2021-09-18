package com.codeborne.selenide;

import com.codeborne.selenide.conditions.clipboard.Content;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @since 5.23.0
 */
@ParametersAreNonnullByDefault
public class ClipboardConditions {
  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<Clipboard> content(String expectedContent) {
    return new Content(expectedContent);
  }
}
