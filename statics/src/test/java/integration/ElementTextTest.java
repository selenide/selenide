package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.PatternSyntaxException;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exactOwnTextCaseSensitive;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.ownTextCaseSensitive;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.partialTextCaseSensitive;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.textCaseSensitive;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ElementTextTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_divs.html");
  }

  @Test
  void canCheckTextOfElement() {
    $("#child_div1").shouldHave(text("Son"));
    $("#grandchild_div").shouldHave(exactText("Granddaughter"));
  }
  @Test
  void canCheckTextOfElement_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
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
  void canGetTextOfElementWithChildren_partial() {
    $("#parent_div").shouldHave(partialText("g papa\n" +
      "Son\n" +
      "Daughter\n" +
      "Grand"));
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
    $("#parent_div").shouldNotHave(ownText("papa"));
    $("#parent_div").shouldNotHave(ownText("Son"));
    $("#parent_div").shouldNotHave(ownText("Daughter"));
  }

  @Test
  void canCheckTextOfElementWithoutChildren_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
    $("#child_div1").shouldHave(ownText("So"));
    $("#child_div2").shouldHave(ownText("Daugh"));
    $("#parent_div").shouldHave(ownText("ig pap"));
    $("#parent_div").shouldNotHave(ownText("on"));
    $("#parent_div").shouldNotHave(ownText("aughte"));
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
    $("#child_div1").should(matchText("S.n"));
    $("#child_div1").should(matchText("So.+"));
    $("#child_div1").should(matchText("So\\w"));
    $("#child_div1").should(matchText("S\\wn"));
  }

  @Test
  void canCheckTextByRegularExpression_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
    $("#child_div1").should(matchText("So"));
    $("#child_div1").should(matchText("S."));
    $("#child_div1").should(matchText("\\wn"));
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

  @Test
  void text_ignoresCase() {
    $("#upper-case").shouldHave(text("this is sparta!"));
    $("#upper-case").shouldHave(text("THIS IS SPARTA!"));
  }

  @Test
  void partialText_ignoresCase() {
    $("#upper-case").shouldHave(partialText("IS IS SPA"));
    $("#upper-case").shouldHave(partialText("is is spa"));
  }

  @Test
  void exactText_ignoresCase() {
    $("#upper-case").shouldHave(exactText("this is sparta!"));
    $("#upper-case").shouldHave(exactText("THIS IS SPARTA!"));
    $("#upper-case").shouldNotHave(exactText("IS IS SPA"));
  }

  @Test
  void text_caseSensitive() {
    $("#upper-case").shouldNotHave(textCaseSensitive("This is Sparta!"));
    $("#upper-case").shouldHave(textCaseSensitive("THIS IS SPARTA!"));
    $("#upper-case").shouldNotHave(textCaseSensitive("IS IS SPA"));
  }

  @Test
  void partialText_caseSensitive() {
    $("#upper-case").shouldHave(partialTextCaseSensitive("IS IS SPA"));
  }

  @Test
  void exactText_caseSensitive() {
    $("#upper-case").shouldNotHave(exactTextCaseSensitive("this is sparta!"));
    $("#upper-case").shouldHave(exactTextCaseSensitive("THIS IS SPARTA!"));
    $("#upper-case").shouldNotHave(exactTextCaseSensitive("IS IS SPA"));
  }
}
