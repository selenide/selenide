package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public class Arguments {
  @Nullable
  private final Object @Nullable [] args;

  public Arguments(@Nullable Object @Nullable... args) {
    this.args = args;
  }

  public <T> Optional<T> ofType(Class<T> klass) {
    if (args == null) return Optional.empty();

    for (Object arg : args) {
      if (arg != null && klass.isAssignableFrom(arg.getClass()))
        //noinspection unchecked
        return Optional.of((T) arg);
    }
    return Optional.empty();
  }

  @Nullable
  public <T> T nth(int index) {
    if (args == null) {
      throw new IllegalArgumentException("Missing arguments");
    }
    if (index >= args.length) {
      throw new IllegalArgumentException("Missing argument #" + index + " in " + Arrays.toString(args));
    }
    //noinspection unchecked
    return (T) args[index];
  }

  public long getTimeoutMs(long defaultTimeout) {
    return ofType(Duration.class).map(Duration::toMillis)
      .orElseGet(() ->
        ofType(HasTimeout.class).map(HasTimeout::timeout).map(Duration::toMillis)
          .orElseGet(() ->
            ofType(Long.class).orElse(defaultTimeout))
      );
  }
}
