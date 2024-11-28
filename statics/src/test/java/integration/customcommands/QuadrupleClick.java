package integration.customcommands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static integration.customcommands.MyFramework.quadrupleClickCounter;

class QuadrupleClick extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    quadrupleClickCounter.incrementAndGet();
  }
}
