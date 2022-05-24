package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.PatternSyntaxException;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exactOwnTextCaseSensitive;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.ownTextCaseSensitive;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    $("#parent_div").shouldNotHave(exactOwnText("Daughter"));
  }

  @Test
  void canCheckCaseSensitiveTextOfElementWithoutChildren() {
    $("#child_div1").shouldHave(ownTextCaseSensitive("Son"));
    $("#child_div1").shouldHave(ownTextCaseSensitive("So"));
    $("#child_div1").shouldNotHave(ownTextCaseSensitive("son"));
    $("#child_div2").shouldHave(ownTextCaseSensitive("Daughter"));
    $("#parent_div").shouldNotHave(ownTextCaseSensitive("Big Papa"));
    $("#parent_div").shouldNotHave(ownTextCaseSensitive("Papa"));
    $("#parent_div").shouldHave(ownTextCaseSensitive("papa"));
    $("#parent_div").shouldNotHave(ownTextCaseSensitive("Son"));
    $("#parent_div").shouldNotHave(ownTextCaseSensitive("Daughter"));
  }
  
  @Test
  void canCheckExactTextCaseSensitiveOfElementWithoutChildren() {
    $("#child_div1").shouldHave(exactOwnTextCaseSensitive("Son"));
    $("#child_div1").shouldNotHave(exactOwnTextCaseSensitive("son"));
    $("#child_div1").shouldNotHave(exactOwnTextCaseSensitive("So"));
    $("#child_div2").shouldHave(exactOwnTextCaseSensitive("Daughter"));
    $("#parent_div").shouldHave(exactOwnTextCaseSensitive("Big papa"));
    $("#parent_div").shouldNotHave(exactOwnTextCaseSensitive("papa"));
    $("#parent_div").shouldNotHave(exactOwnTextCaseSensitive("Son"));
    $("#parent_div").shouldNotHave(exactOwnTextCaseSensitive("son"));
    $("#parent_div").shouldNotHave(exactOwnTextCaseSensitive("So"));
    $("#parent_div").shouldNotHave(exactOwnTextCaseSensitive("Daughter"));
  }

  @Test
  void canCheckTextByRegularExpression() {
    $("#child_div1").should(matchText("Son"));
    $("#child_div1").should(matchText("So.+"));
    $("#child_div1").should(matchText("So\\w"));
  }

  @Test
  void cannotUseEmptyRegularExpression() {
    assertThatThrownBy(() -> $("#child_div1").should(matchText("")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Argument must not be null or empty string");
  }

  @Test
  void cannotUseInvalidRegularExpression() {
    assertThatThrownBy(() -> $("#child_div1").should(matchText("{")))
      .isInstanceOf(PatternSyntaxException.class);
  }
}
