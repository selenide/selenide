package com.codeborne.selenide.commands.ancestor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class AncestorWithClassRule extends SelectorValidation implements AncestorRule {

  @Override
  @Nonnull
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (isCssClass(selector)) {
      String xpath = format(
        "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][%s]",
        selector.substring(1),
        index
      );
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
