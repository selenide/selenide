package integration;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.concurrent.atomic.AtomicInteger;

class OverrideCommandsTest extends ITest {
  private AtomicInteger clickCounter = new AtomicInteger();

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
    assertThat(clickCounter.get())
      .isEqualTo(2);
  }

  private class MyClick extends Click {
    @Override
    protected void click(Driver driver, WebElement element) {
      super.click(driver, element);
      clickCounter.incrementAndGet();
    }
  }
}
