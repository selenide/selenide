package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith({SoftAssertsExtension.class})
public class SoftAssertInParametrizedTest extends IntegrationTest {
  private final TestPage page = new TestPage();

  @BeforeEach
  void setUp() {
    givenHtml(
      "<form>Type username:",
      "<input name='username'></input>",
      "</form>"
    );
  }

  @ParameterizedTest
  @ValueSource(strings = { "foo", "bar"})
  public void testOne(String query) {
    page.used.val(query).pressEnter();
  }

  private static class TestPage {
    static {
      Configuration.assertionMode = SOFT;
    }
    public SelenideElement unused = $("form").$x(".//div/span");
    public SelenideElement used = $("form").$("[name=username]");
  }
}
