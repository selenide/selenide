package integration;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.concurrent.atomic.AtomicInteger;

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
    assertThat(clickCounter.get())
      .isEqualTo(2);
  }

  private class MyClick extends Click {
    @Override
    protected void click(Context context, WebElement element) {
      super.click(context, element);
      clickCounter.incrementAndGet();
    }
  }
}
