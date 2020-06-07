package integration.customcommands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static integration.customcommands.MyFramework.tripleClickCounter;

@ParametersAreNonnullByDefault
class TripleClick implements Command<MySelenideElement> {
  @Override
  @Nonnull
  public MySelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    tripleClickCounter.incrementAndGet();
    return (MySelenideElement) proxy;
  }
}
