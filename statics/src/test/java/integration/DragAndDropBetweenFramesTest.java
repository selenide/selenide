package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DragAndDropBetweenFramesTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("page_with_draggable_frames.html");
  }

  @Test
  public void dragAndDropBetweenFrames() {
    switchTo().frame("top-frame");
    WebElement source = $("#drag1").toWebElement();
    switchTo().parentFrame();
    switchTo().frame("bottom-frame");
    WebElement target = $("#div2").toWebElement();

    switchTo().parentFrame();
    switchTo().frame("top-frame");

    assertThatThrownBy(() -> {
      $(source).dragAndDropTo(target);
      $("#div2").$("#drag1").should(appear);
    })
      .as("This reproduces the issue. Actually this code should not throw any exceptions.")
      .isInstanceOf(ElementShould.class);
  }
}
