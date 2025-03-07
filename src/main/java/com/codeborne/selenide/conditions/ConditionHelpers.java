package com.codeborne.selenide.conditions;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class ConditionHelpers {

  @SafeVarargs
  public static <T> List<T> merge(T first, T second, T... others) {
    List<T> result = new ArrayList<>(2 + others.length);
    result.add(first);
    result.add(second);
    if (others.length > 0) result.addAll(asList(others));
    return unmodifiableList(result);
  }
}
