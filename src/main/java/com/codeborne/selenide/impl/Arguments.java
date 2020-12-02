package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class Arguments {
  private final Object[] args;

  public Arguments(@Nullable Object[] args) {
    this.args = args;
  }

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

  public int length() {
    return args == null ? 0 : args.length;
  }

  @CheckReturnValue
  @Nonnull
  public <T> Optional<T> ofType(@Nonnull Class<T> klass) {
    if (args == null) return Optional.empty();

    for (Object arg : args) {
      if (klass.isAssignableFrom(arg.getClass()))
        //noinspection unchecked
        return Optional.of((T) arg);
    }
    return Optional.empty();
  }
}
