package integration;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

final class ExecuteMethodTest extends ITest {

  @BeforeEach
  void setUp() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanExecuteCustomCommand() {
    $("#username").scrollTo()
      .setValue("value")
      .execute(new CustomSetValueCommand("custom value"))
      .pressEnter()
      .execute(new TripleClick());
    assertThat($("#username-mirror").text())
      .startsWith("custom value");
  }

  @Test
  void userCanExecuteCustomCommandWithGivenTimeout() {
    setTimeout(1);

    $("#dynamic-content2")
      .execute(new TripleClick(), Duration.ofSeconds(3));
  }

  @Test
  void executeMethodWithGivenTimeoutThrowsErrorAfterTimeout() {
    setTimeout(1);

    long timeout = 1100;
    long startMs = System.currentTimeMillis();
    assertThatCode(() -> $("#non-existing-element").execute(new TripleClick(), Duration.ofMillis(timeout)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Timeout: 1.100 s.");
    long elapsedTimeMs = System.currentTimeMillis() - startMs;
    assertThat(elapsedTimeMs).isBetween(timeout, timeout * 5);
  }

  @Test
  void canExecuteStandardCommand() {
    $("#username").execute(new Click());
  }

  @Test
  void canExecuteStandardCommand_withCustomTimeout() {
    $("#username").execute(new Click(), Duration.ofSeconds(1));
  }

  @Test
  void executeMethodDoesNotPassArgsToCustomCommand() {
    AtomicReference<Object[]> passedArgsRef = new AtomicReference<>();
    $("#username").execute(
      new Command<Void>() {
        @Override
        @Nullable
        @CanIgnoreReturnValue
        public Void execute(SelenideElement proxy,
                            WebElementSource locator,
                            Object @Nullable [] args) {
          passedArgsRef.set(args);
          return null;
        }
      }
    );
    assertThat(passedArgsRef.get()).isEmpty();
  }

  @Test
  void executeMethodWithGivenTimeoutDoesNotPassArgsToCustomCommand() {
    AtomicReference<Object[]> passedArgsRef = new AtomicReference<>();
    $("#username").execute(
      new Command<Void>() {
        @Override
        @Nullable
        @CanIgnoreReturnValue
        public Void execute(SelenideElement proxy,
                            WebElementSource locator,
                            Object @Nullable [] args) {
          passedArgsRef.set(args);
          return null;
        }
      },
      Duration.ofMillis(1)
    );
    assertThat(passedArgsRef.get()).isEmpty();
  }

  private static final class TripleClick extends FluentCommand {
    @Override
    protected void execute(WebElementSource locator, Object @Nullable [] args) {
      WebElement element = locator.findAndAssertElementIsInteractable();
      element.click();
      element.click();
      element.click();
    }
  }

  private static final class CustomSetValueCommand extends FluentCommand {
    private final String value;

    CustomSetValueCommand(String value) {
      this.value = value;
    }

    @Override
    protected void execute(WebElementSource locator, Object @Nullable [] args) {
      WebElement webElement = locator.getWebElement();
      webElement.clear();
      webElement.sendKeys(value);
    }
  }
}
