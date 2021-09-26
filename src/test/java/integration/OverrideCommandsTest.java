package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

final class OverrideCommandsTest extends ITest {
  private final AtomicInteger clickCounter = new AtomicInteger();

  @BeforeEach
  void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @AfterEach
  void tearDown() {
    Commands.getInstance().add("click", new Click());
  }

  @Test
  void userCanOverrideAnyCommand() {
    Commands.getInstance().add("click", new MyClick());
    $("#valid-image").click();
    $("#invalid-image").click();
    assertThat(clickCounter.get()).isEqualTo(2);
  }

  @ParametersAreNonnullByDefault
  private class MyClick extends Click {

    @Override
    @Nullable
    public Void execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
      clickCounter.incrementAndGet();
      return null;
    }
  }
}
