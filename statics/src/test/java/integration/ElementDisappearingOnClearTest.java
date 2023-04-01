package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.RepeatedTest;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

final class ElementDisappearingOnClearTest extends IntegrationTest {
  @RepeatedTest(10)
  void clearElementThatDisappearsAfterClearing() {
    SecretPage page = openFile("page_with_input_disappearing_on_clear.html", SecretPage.class);
    page.greeting.shouldBe(visible).shouldHave(value("Bond. James Bond."));
    page.greeting.clear();
    page.greeting.should(disappear);
  }

  private static class SecretPage {
    SelenideElement greeting = $("#greeting");
  }
}
