package integration;

import com.codeborne.selenide.TypeOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static java.time.Duration.ofMillis;

class TypeOptionsTest extends ITest {
  @BeforeEach
  void openAndInitTestPage() {
    openFile("page_with_inputevents_on_clear.html");
  }

  @Test
  void typeOptions() {
    $("#username_input").setValue("test");
    $("#username_input").type("abc");
    $("#username_input").shouldHave(attribute("value", "abc"));

    $("#username_input").type(TypeOptions.text("test"));
    $("#username_input").shouldHave(attribute("value", "test"));

    $("#username_input").type(TypeOptions.text("abc").withDelay(ofMillis(100)).clearFirst(false));
    $("#username_input").shouldHave(attribute("value", "testabc"));
  }
}
