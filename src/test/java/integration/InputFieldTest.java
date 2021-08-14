package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.value;

final class InputFieldTest extends ITest {
  @BeforeEach
  void setup() {
    openFile("html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  void selenideClearTest() {
    SelenideElement input = $("#id1");
    input.shouldBe(empty);

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    input.shouldHave(value("456"));

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    input.shouldHave(value("456"));

    input.setValue("456");
    input.setValue(null);
    input.shouldBe(empty);
  }
}
