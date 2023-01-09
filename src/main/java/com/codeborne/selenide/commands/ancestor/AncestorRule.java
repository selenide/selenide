package com.codeborne.selenide.commands.ancestor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface AncestorRule {
  @Nonnull
  Optional<AncestorResult> evaluate(String selector, int index);
}
