package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.pseudo;
import static org.assertj.core.api.Assertions.assertThat;

public class PseudoTest extends ITest {

  private static final String LOCATOR_H1 = "h1";
  private static final String LOCATOR_H2 = "h2";
  private static final String LOCATOR_ABBR = "abbr";
  private static final String LOCATOR_P = "p";

  private static final String PSEUDO_FIRST_LETTER = ":first-letter";
  private static final String PSEUDO_BEFORE = ":before";
  private static final String PSEUDO_AFTER = ":after";

  private static final String PROPERTY_COLOR = "color";
  private static final String PROPERTY_CONTENT = "content";

  private static final String COLOR_RED = "rgb(255, 0, 0)";
  private static final String CONTENT_EXPECTED_VALUE = "\"beforeContent\"";
  private static final String CONTENT_NO_VALUE = "none";

  @BeforeEach
  void openTestPage() {
    openFile("page_with_pseudo_elements.html");
  }

  @Test
  void shouldHavePseudo() {
    $(LOCATOR_H1).shouldHave(pseudo(PSEUDO_FIRST_LETTER, PROPERTY_COLOR, COLOR_RED));
    $(LOCATOR_H2).shouldNotHave(pseudo(PSEUDO_FIRST_LETTER, PROPERTY_COLOR, COLOR_RED));
    $(LOCATOR_ABBR).shouldHave(pseudo(PSEUDO_BEFORE, PROPERTY_CONTENT, CONTENT_EXPECTED_VALUE));
    $(LOCATOR_P).shouldNotHave(pseudo(PSEUDO_BEFORE, PROPERTY_CONTENT, CONTENT_EXPECTED_VALUE));
    $(LOCATOR_ABBR).shouldHave(pseudo(PSEUDO_BEFORE, CONTENT_EXPECTED_VALUE));
    $(LOCATOR_P).shouldNotHave(pseudo(PSEUDO_BEFORE, CONTENT_EXPECTED_VALUE));
  }

  @Test
  void getPseudo() {
    assertThat($(LOCATOR_H1).pseudo(PSEUDO_FIRST_LETTER, PROPERTY_COLOR)).isEqualTo(COLOR_RED);
    assertThat($(LOCATOR_ABBR).pseudo(PSEUDO_BEFORE, PROPERTY_CONTENT)).isEqualTo(CONTENT_EXPECTED_VALUE);
    assertThat($(LOCATOR_P).pseudo(PSEUDO_AFTER, PROPERTY_CONTENT)).isEqualTo(CONTENT_NO_VALUE);
    assertThat($(LOCATOR_ABBR).pseudo(PSEUDO_BEFORE)).isEqualTo(CONTENT_EXPECTED_VALUE);
    assertThat($(LOCATOR_P).pseudo(PSEUDO_AFTER)).isEqualTo(CONTENT_NO_VALUE);
  }
}
