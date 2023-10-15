package com.codeborne.selenide;

import java.time.Duration;

public class TypeOptions {
  private static final Duration DEFAULT_DELAY_WHILE_TYPING = Duration.ofMillis(200);

  private final CharSequence charSequence;
  private final boolean shouldClearFieldBeforeTyping;
  private final Duration timeDelayWhileTyping;

  protected TypeOptions(CharSequence charSequence, Duration timeDelayWhileTyping, boolean shouldClearFieldBeforeTyping) {
    this.charSequence = charSequence;
    this.timeDelayWhileTyping = timeDelayWhileTyping;
    this.shouldClearFieldBeforeTyping = shouldClearFieldBeforeTyping;
  }

  public static TypeOptions text(CharSequence textToType) {
    return new TypeOptions(textToType, DEFAULT_DELAY_WHILE_TYPING, true);
  }

  public TypeOptions withDelay(Duration timeDelayWhileTyping) {
    return new TypeOptions(charSequence, timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }

  public TypeOptions clearFirst(boolean shouldClearFieldBeforeTyping) {
    return new TypeOptions(charSequence, timeDelayWhileTyping, shouldClearFieldBeforeTyping);
  }

  public CharSequence textToType() {
    return this.charSequence;
  }

  public boolean shouldClearFieldBeforeTyping() {
    return this.shouldClearFieldBeforeTyping;
  }

  public Duration timeDelay() {
    return this.timeDelayWhileTyping;
  }
}
