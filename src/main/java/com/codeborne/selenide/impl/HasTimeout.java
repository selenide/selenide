package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.time.Duration;

/**
 * @since 6.6.3
 */
public interface HasTimeout {
  @Nullable
  @CheckReturnValue
  Duration timeout();
}
