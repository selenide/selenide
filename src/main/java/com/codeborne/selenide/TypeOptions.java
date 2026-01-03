package com.codeborne.selenide;

import java.time.Duration;

import static com.codeborne.selenide.ValueMasker.mask;

public record TypeOptions(
  CharSequence textToType,
  CharSequence displayText,
  Duration timeDelayWhileTyping,
  boolean shouldClearFieldBeforeTyping
) {
  private static final Duration DEFAULT_DELAY_WHILE_TYPING = Duration.ofMillis(200);

  public static TypeOptions text(CharSequence textToType) {
    return new TypeOptions(textToType, textToType, DEFAULT_DELAY_WHILE_TYPING, true);
  }

  public TypeOptions withDelay(Duration timeDelayWhileTyping) {
    return new TypeOptions(textToType, displayText, timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }

  public TypeOptions clearFirst(boolean shouldClearFieldBeforeTyping) {
    return new TypeOptions(textToType, displayText, timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }

  public TypeOptions sensitive() {
    return new TypeOptions(textToType, mask(textToType), timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }

  @Override
  public String toString() {
    return String.format("\"%s\" (delay: %s, clearFirst: %s)", displayText, timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }
}
