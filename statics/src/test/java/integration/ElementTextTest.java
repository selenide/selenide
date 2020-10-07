package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

final class ElementTextTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_divs.html");
  }

  @Test
  void canGetTextOfElement() {
    $("#child_div1").shouldHave(text("Son"));
    $("#grandchild_div").shouldHave(exactText("Granddaughter"));
  }

  @Test
  void canGetTextOfElementWithChildren() {
    $("#parent_div").shouldHave(exactText("Big papa\n" +
      "Son\n" +
      "Daughter\n" +
      "Granddaughter"));
  }

  @Test
  void canGetTextOfElementWithoutChildren() {
    assertThat($("#child_div1").getOwnText()).isEqualTo("Son");
    assertThat($("#child_div2").getOwnText()).isEqualTo("Daughter  \n\n  ");
    assertThat($("#parent_div").getOwnText()).isEqualTo("\n" +
      "  Big papa\n" +
      "  \n" +
      "\n" +
      "  \n" +
      "\n");
  }

  @Test
  void canCheckTextOfElementWithoutChildren() {
    $("#child_div1").shouldHave(ownText("Son"));
    $("#child_div2").shouldHave(ownText("Daughter"));
    $("#parent_div").shouldHave(ownText("Big papa"));
    $("#parent_div").shouldHave(ownText("papa"));
    $("#parent_div").shouldNotHave(ownText("Son"));
    $("#parent_div").shouldNotHave(ownText("Daughter"));
  }

  @Test
  void canCheckExactTextOfElementWithoutChildren() {
    $("#child_div1").shouldHave(exactOwnText("Son"));
    $("#child_div2").shouldHave(exactOwnText("Daughter"));
    $("#parent_div").shouldHave(exactOwnText("Big papa"));
    $("#parent_div").shouldNotHave(exactOwnText("papa"));
    $("#parent_div").shouldNotHave(exactOwnText("Son"));
    $("#parent_div").shouldNotHave(exactOwnText("Daughter"));  }
}
