package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@ParametersAreNonnullByDefault
class Util {
  @SuppressWarnings("unchecked")
  @CheckReturnValue
  @Nonnull
  static <T> T firstOf(@Nullable Object[] args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing arguments");
    }
    return (T) args[0];
  }

  @CheckReturnValue
  @Nonnull
  static List<Condition> argsToConditions(@Nullable Object[] args) {
    if (args == null) return emptyList();

    List<Condition> conditions = new ArrayList<>(args.length);
    for (Object arg : args) {
      if (arg instanceof Condition)
        conditions.add((Condition) arg);
      else if (arg instanceof Condition[])
        conditions.addAll(asList((Condition[]) arg));
      else if (!(arg instanceof String || arg instanceof Long))
        throw new IllegalArgumentException("Unknown parameter: " + arg);
    }
    return conditions;
  }
}
