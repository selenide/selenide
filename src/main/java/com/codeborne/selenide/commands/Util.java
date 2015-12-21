package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class Util {
  static List<Condition> argsToConditions(Object[] args) {
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
