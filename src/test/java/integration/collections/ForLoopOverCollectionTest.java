package integration.collections;

import com.codeborne.selenide.SelenideElement;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class ForLoopOverCollectionTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void canIterateCollection() {
    $$(".element").shouldHave(size(3));

    /*
     * Not recommended.
     * Please, don't use IFs and LOOPs in your tests.
     * It's a sign of a bad test design.
     */
    for (SelenideElement element : $$(".element")) {
      element.shouldBe(visible);
      element.shouldHave(or("", text("One"), text("Two"), text("Three")));
    }
  }
}
