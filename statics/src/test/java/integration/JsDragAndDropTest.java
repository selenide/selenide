package integration;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;

public class JsDragAndDropTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("draggable.html");
  }

  @Test
  public void checkDragAndDropByJS() {
    $("#drag1").dragAndDropToByJs("#div2");
    $("#div2").$("#drag1").shouldBe(Condition.appear);
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }

}
