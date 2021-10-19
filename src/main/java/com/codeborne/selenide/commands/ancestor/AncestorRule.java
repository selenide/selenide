package com.codeborne.selenide.commands.ancestor;

import java.util.Optional;

public interface AncestorRule {
  Optional<AncestorResult> evaluate(String selector, int index);
}
