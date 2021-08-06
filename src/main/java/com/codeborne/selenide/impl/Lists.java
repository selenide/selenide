package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class Lists {
  @SafeVarargs
  @CheckReturnValue
  @Nonnull
  public static <T> List<T> list(T first, T... others) {
    List<T> result = new ArrayList<>(1 + others.length);
    result.add(first);
    Collections.addAll(result, others);
    return unmodifiableList(result);
  }
}
