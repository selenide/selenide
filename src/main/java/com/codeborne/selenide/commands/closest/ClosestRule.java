package com.codeborne.selenide.commands.closest;

public interface ClosestRule {
  boolean evaluate(String selector);

  ClosestResult getClosestResult();
}
