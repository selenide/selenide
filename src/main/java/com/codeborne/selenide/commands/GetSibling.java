package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.sibling.Sibling;
import com.codeborne.selenide.impl.WebElementSource;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GetSibling implements Command<SelenideElement> {

  private final List<Sibling> siblings;

  public GetSibling(List<Sibling> siblings) {
    this.siblings = siblings;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (args == null) {
      throw new IllegalArgumentException("Missing arguments");
    }
    return siblings.stream()
      .filter(sibling -> sibling.canExecute(args))
      .findFirst()
      .map(sibling -> sibling.execute(proxy, locator, args))
      .orElseThrow(() -> new NotImplementedException("Not implemented"));
  }
}
