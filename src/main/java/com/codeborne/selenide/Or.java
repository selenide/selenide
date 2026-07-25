package com.codeborne.selenide;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static java.util.stream.Collectors.joining;

class Or<T> implements ObjectCondition<T> {
  private final ObjectCondition<T> first;
  private final ObjectCondition<T> second;

  Or(ObjectCondition<T> first, ObjectCondition<T> second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public String description() {
    return "%s or %s".formatted(first.description(), second.description());
  }

  @Override
  public String negativeDescription() {
    return "not(%s)".formatted(description());
  }

  @Override
  public CheckResult check(T object) {
    List<CheckResult> results = new ArrayList<>();
    for (ObjectCondition<T> c : List.of(first, second)) {
      CheckResult check = c.check(object);
      if (check.verdict() == ACCEPT) {
        return check;
      }
      else {
        results.add(check);
      }
    }

    String actualValues = results.stream().map(check -> String.valueOf(check.actualValue())).collect(joining(", "));
    return new CheckResult(REJECT, actualValues);
  }

  @Override
  public String expectedValue() {
    return "%s or %s".formatted(first.expectedValue(), second.expectedValue());
  }

  @Override
  public String describe(T object) {
    return first.describe(object);
  }
}
