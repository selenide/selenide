package com.codeborne.selenide;

import java.time.Duration;

public class TypeOptions {

  private final CharSequence charSequence;
  private boolean shouldClearFieldBeforeTyping;
  private Duration timeDelayWhileTyping;
  private static final Duration DEFAULT_DELAY_WHILE_TYPING = Duration.ofMillis(200);

  protected TypeOptions(CharSequence charSequence, Duration timeDelayWhileTyping, boolean shouldClearFieldBeforeTyping) {
    this.charSequence = charSequence;
    this.timeDelayWhileTyping = timeDelayWhileTyping;
    this.shouldClearFieldBeforeTyping = shouldClearFieldBeforeTyping;
  }

  public static TypeOptions text(CharSequence textToType) {
    return new TypeOptions(textToType, DEFAULT_DELAY_WHILE_TYPING, true);
  }

  public TypeOptions withDelay(Duration timeDelayWhileTyping) {
    this.timeDelayWhileTyping = timeDelayWhileTyping;
    return this;
  }

  public TypeOptions clearFirst(boolean shouldClearFieldBeforeTyping) {
    this.shouldClearFieldBeforeTyping = shouldClearFieldBeforeTyping;
    return this;
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
