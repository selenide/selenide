package integration;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

final class ExecuteMethodTest extends ITest {

  @Test
  void userCanExecuteCustomCommand() {
    openFile("page_with_selects_without_jquery.html");

    $("#username").scrollTo()
      .setValue("value")
      .execute(new CustomSetValueCommand("custom value"))
      .pressEnter()
      .execute(new CustomDoubleClickCommand());
    assertThat($("#username-mirror").text())
      .startsWith("custom value");
  }

  @Test
  void userCanExecuteCustomCommandWithGivenTimeout() {
    setTimeout(1);
    openFile("long_ajax_request.html");
    $("#loading").shouldNot(exist);
    $(byText("Run long request")).click();

    $(byText("Loading..."))
      .execute(new CustomShouldCommand(exist), Duration.ofSeconds(5))
      .execute(new CustomShouldNotCommand(exist), Duration.ofSeconds(5));
  }

  @Test
  void executeMethodWithGivenTimeoutThrowsErrorAfterTimeout() {
    setTimeout(1);
    openFile("page_with_selects_without_jquery.html");

    final Duration timeoutDuration = Duration.ofMillis(5000);
    final Command<SelenideElement> customCommand = new CustomShouldCommand(exist);
    final SelenideElement element = $(byName("non-existing-element"));
    final long startMs = System.currentTimeMillis();
    assertThatCode(() -> element.execute(customCommand, timeoutDuration))
      .isInstanceOf(ElementNotFound.class);
    final long timeoutMs = System.currentTimeMillis() - startMs;
    assertThat(timeoutMs)
      .isBetween(5000L, 5999L);
  }

  @ParametersAreNonnullByDefault
  private static final class CustomDoubleClickCommand implements Command<Void> {

    CustomDoubleClickCommand() {
    }

    @Override
    @Nullable
    public Void execute(final SelenideElement proxy,
                        final WebElementSource locator,
                        final @Nullable Object[] args) {
      locator.driver()
        .actions()
        .doubleClick(locator.findAndAssertElementIsInteractable())
        .perform();
      return null;
    }
  }

  @ParametersAreNonnullByDefault
  private static final class CustomSetValueCommand implements Command<SelenideElement> {
    private final String value;

    CustomSetValueCommand(final String value) {
      this.value = value;
    }

    @Override
    @Nonnull
    public SelenideElement execute(final SelenideElement proxy,
                                   final WebElementSource locator,
                                   final @Nullable Object[] args) {
      proxy.clear();
      proxy.sendKeys(value);
      return proxy;
    }
  }

  @ParametersAreNonnullByDefault
  private static final class CustomShouldCommand implements Command<SelenideElement> {
    private final Condition condition;

    CustomShouldCommand(final Condition condition) {
      this.condition = condition;
    }

    @Override
    @Nonnull
    public SelenideElement execute(final SelenideElement proxy,
                                   final WebElementSource locator,
                                   final @Nullable Object[] args) {
      locator.checkCondition("custom ", condition, false);
      return proxy;
    }
  }

  @ParametersAreNonnullByDefault
  private static final class CustomShouldNotCommand implements Command<Void> {
    private final Condition condition;

    CustomShouldNotCommand(final Condition condition) {
      this.condition = condition;
    }

    @Override
    @Nonnull
    public Void execute(final SelenideElement proxy,
                        final WebElementSource locator,
                        final @Nullable Object[] args) {
      locator.checkCondition("custom ", condition, true);
      return null;
    }
  }
}
