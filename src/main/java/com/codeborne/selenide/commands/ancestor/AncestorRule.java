package com.codeborne.selenide.commands.ancestor;

public interface AncestorRule {
  boolean evaluate(String selector, int index);

  AncestorResult getAncestorResult();
}
