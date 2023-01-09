package com.codeborne.selenide.commands.ancestor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class AncestorWithTagRule extends SelectorValidation implements AncestorRule {

  @Override
  @Nonnull
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (!isCssClass(selector) && !isAttribute(selector) && !containsAttributeValue(selector)) {
      String xpath = format("ancestor::%s[%s]", selector, index);
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
