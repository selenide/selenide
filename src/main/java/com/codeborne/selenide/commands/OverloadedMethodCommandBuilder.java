package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public final class OverloadedMethodCommandBuilder<T> {
  private final List<Rule<? extends T>> rules;

  public OverloadedMethodCommandBuilder(int initialCapacity) {
    this.rules = new ArrayList<>(initialCapacity);
  }

  public OverloadedMethodCommandBuilder<T> withRule(Class<?>[][] typesCombinations,
                                                    CommandAction<? extends T> commandAction) {
    this.rules.add(new Rule<>(typesCombinations, commandAction));
    return this;
  }

  @SuppressWarnings("unchecked")
  public Command<T> build() {
    return new OverloadedMethodCommand<>(this.rules.toArray(new Rule[0]));
  }

  private static class OverloadedMethodCommand<T> implements Command<T> {
    private final Rule<? extends T>[] rules;

    private OverloadedMethodCommand(final Rule<? extends T>[] rules) {
      this.rules = rules;
    }

    @Override
    @Nullable
    public T execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
      args = args == null ? NO_ARGS : args;
      for (Rule<? extends T> rule : this.rules) {
        for (Class<?>[] combination : rule.typesCombinations) {
          if (args.length == combination.length) {
            boolean suitableCombination = true;
            for (int idx = 0; idx < args.length; ++idx) {
              if (!(args[idx] == null || combination[idx].isInstance(args[idx]))) {
                suitableCombination = false;
                break;
              }
            }
            if (suitableCombination) {
              return rule.commandAction.apply(proxy, locator, args);
            }
          }
        }
      }
      throw new IllegalArgumentException("Overloading rules not found for args: " + Arrays.toString(args));
    }
  }

  private static class Rule<T> {
    private final Class<?>[][] typesCombinations;
    private final CommandAction<? extends T> commandAction;

    private Rule(Class<?>[][] typesCombinations,
                 CommandAction<? extends T> commandAction) {
      this.typesCombinations = typesCombinations;
      this.commandAction = commandAction;
    }
  }

  @FunctionalInterface
  @ParametersAreNonnullByDefault
  public interface CommandAction<T> {

    T apply(SelenideElement proxy, WebElementSource locator, Object[] args);
  }
}
