package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

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
      else if (!(arg instanceof String || arg instanceof Long || arg instanceof Duration))
        throw new IllegalArgumentException("Unknown parameter: " + arg);
    }
    return conditions;
  }

  @CheckReturnValue
  @Nonnull
  @SafeVarargs
  public static <T> List<T> merge(T first, T... others) {
    List<T> result = new ArrayList<>(1 + others.length);
    result.add(first);
    result.addAll(asList(others));
    return unmodifiableList(result);
  }

  @CheckReturnValue
  @Nonnull
  public static List<Integer> merge(int first, int[] others) {
    List<Integer> result = new ArrayList<>(1 + others.length);
    result.add(first);
    IntStream.of(others).forEach(i -> result.add(i));
    return unmodifiableList(result);
  }
}
