package integration;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

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
    Commands.getInstance().resetDefaults();
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
    protected void click(Driver driver, WebElement element) {
      super.click(driver, element);
      clickCounter.incrementAndGet();
    }
  }
}
