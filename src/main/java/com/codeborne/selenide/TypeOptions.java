package com.codeborne.selenide;

import java.time.Duration;

public class TypeOptions {

  private final CharSequence charSequence;
  private final boolean shouldClearFieldBeforeTyping;
  private final Duration timeDelayWhileTyping;
  private static final Duration DEFAULT_DELAY_WHILE_TYPING = Duration.ofMillis(200);

  protected TypeOptions(CharSequence charSequence, Duration timeDelayWhileTyping, boolean shouldClearFieldBeforeTyping) {
    this.charSequence = charSequence;
    this.timeDelayWhileTyping = timeDelayWhileTyping;
    this.shouldClearFieldBeforeTyping = shouldClearFieldBeforeTyping;
  }

  public static TypeOptions with(CharSequence textToType, Duration delayWhileTyping, boolean shouldClearFieldBeforeTyping) {
    return new TypeOptions(textToType, delayWhileTyping, shouldClearFieldBeforeTyping);
  }

  public static TypeOptions with(CharSequence textToType) {
    return new TypeOptions(textToType, DEFAULT_DELAY_WHILE_TYPING, true);
  }

  public static TypeOptions withDelay(CharSequence textToType, Duration delayWhileTyping) {
    return new TypeOptions(textToType, delayWhileTyping, true);
  }

  public static TypeOptions withoutClearingField(CharSequence textToType) {
    return new TypeOptions(textToType, DEFAULT_DELAY_WHILE_TYPING, false);
  }

  public String textToType() {
    return String.valueOf(this.charSequence);
  }

  public boolean shouldClearFieldBeforeTyping() {
    return this.shouldClearFieldBeforeTyping;
  }

  public Duration timeDelay() {
    return this.timeDelayWhileTyping;
  }
}
