package com.codeborne.selenide.impl;

import javax.annotation.Nullable;
import java.util.Arrays;

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
}
