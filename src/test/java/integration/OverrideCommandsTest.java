package integration;

import java.util.concurrent.atomic.AtomicInteger;

import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;

class OverrideCommandsTest extends IntegrationTest {
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
    Assertions.assertEquals(2, clickCounter.get());
  }

  private class MyClick extends Click {
    @Override
    protected void click(WebElement element) {
      super.click(element);
      clickCounter.incrementAndGet();
    }
  }
}
