package com.codeborne.selenide.commands;

import com.codeborne.selenide.WebElementCondition;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public class Util {
  public static int size(@Nullable Object @Nullable [] args) {
    return args == null ? 0 : args.length;
  }

  @SuppressWarnings("unchecked")
  public static <T> T firstOf(Object @Nullable [] args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing arguments");
    }
    return (T) args[0];
  }

  public static List<WebElementCondition> argsToConditions(Object @Nullable [] args) {
    if (args == null) return emptyList();

    List<WebElementCondition> conditions = new ArrayList<>(args.length);
    for (Object arg : args) {
      if (arg instanceof WebElementCondition conditionArgument)
        conditions.add(conditionArgument);
      else if (arg instanceof WebElementCondition[] conditionsArray)
        conditions.addAll(asList(conditionsArray));
      else if (!(arg instanceof String || arg instanceof Long || arg instanceof Duration))
        throw new IllegalArgumentException("Unknown parameter: " + arg);
    }
    return conditions;
  }

  @SafeVarargs
  public static <T> List<T> merge(T first, T... others) {
    List<T> result = new ArrayList<>(1 + others.length);
    result.add(first);
    result.addAll(asList(others));
    return unmodifiableList(result);
  }

  public static List<Integer> merge(int first, int[] others) {
    List<Integer> result = new ArrayList<>(1 + others.length);
    result.add(first);
    IntStream.of(others).forEach(i -> result.add(i));
    return unmodifiableList(result);
  }

  public static Map<Object, Object> mergeMaps(Map<?, ?> first, Map<?, ?> second) {
    Map<Object, Object> result = new HashMap<>(first.size() + second.size());
    result.putAll(first);
    result.putAll(second);
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> T cast(Object value) {
    return (T) value;
  }

  public static <T> String arrayToString(List<T> values) {
    return values.stream().map(Objects::toString).collect(joining(","));
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> classOf(T... reified) {
    if (reified.length > 0) {
      throw new IllegalArgumentException("Please don't pass any values here. Java will detect page object class automagically.");
    }
    return (Class<T>) reified.getClass().getComponentType();
  }
}
