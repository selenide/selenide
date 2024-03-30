package integration;

import com.codeborne.selenide.As;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.EventsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;

final class AliasesInReportTest extends IntegrationTest {
  private final EventsCollector report = new EventsCollector();

  @BeforeEach
  void setUp() {
    open();
    SelenideLogger.addListener("aliasesTest", report);
  }

  @AfterEach
  void tearDown() {
    SelenideLogger.removeListener("aliasesTest");
  }

  @Test
  void elementWithoutAlias() {
    $("h1").shouldNot(exist);
    assertName("h1");
  }

  @Test
  void elementWithAlias() {
    $("h1").as("The header").shouldNot(exist);
    assertName("The header");
  }

  @Test
  void elementFoundFromCollection() {
    $$("h1")
      .find(cssClass("icon"))
      .shouldNot(exist);
    assertName("h1.findBy(css class \"icon\")");
  }

  @Test
  void elementFoundFromNamedCollection() {
    $$("h1")
      .as("Greetings")
      .find(cssClass("icon"))
      .shouldNot(exist);
    assertName("Greetings.findBy(css class \"icon\")");
  }

  @Test
  void elementWithAliasFoundFromCollection() {
    $$("h1")
      .find(cssClass("icon"))
      .as("The icon")
      .shouldNot(exist);
    assertName("The icon");
  }

  @Test
  void lastElementOfCollectionWithAlias() {
    $$("h1")
      .last()
      .as("The icon")
      .shouldNot(exist);
    assertName("The icon");
  }

  @Test
  void lastElementOfNamedCollection() {
    $$("h1")
      .as("Greetings")
      .last()
      .shouldNot(exist);
    assertName("Greetings:last");
  }

  @Test
  void lastElementOfNamedCollectionWithAlias() {
    $$("h1")
      .as("Greetings")
      .last()
      .as("The icon")
      .shouldNot(exist);
    assertName("The icon");
  }

  @Test
  void firstElementOfNamedCollection() {
    $$("h1")
      .as("Greetings")
      .first()
      .shouldNot(exist);
    assertName("Greetings[0]");
  }

  @Test
  void firstElementOfNamedCollectionWithAlias() {
    $$("h1")
      .as("Greetings")
      .first()
      .as("The first")
      .shouldNot(exist);
    assertName("The first");
  }

  @Test
  void nthElementOfNamedCollection() {
    $$("h1")
      .as("Greetings")
      .get(42)
      .shouldNot(exist);
    assertName("Greetings[42]");
  }

  @Test
  void nthElementOfNamedCollectionWithAlias() {
    $$("h1")
      .as("Clouds")
      .get(9)
      .as("Clould number nine")
      .shouldNot(exist);
    assertName("Clould number nine");
  }

  @Test
  void getSelectedOptionWithAlias() {
    $("select")
      .as("Heaven")
      .getSelectedOption()
      .as("Clould number nine")
      .shouldNot(exist);
    assertName("Clould number nine");
  }

  @Test
  void webElementWrapper() {
    WebElement web = $("body").toWebElement();

    $(web)
      .as("The body")
      .should(exist);
    assertName("The body");
  }

  @Test
  void pageObjectFieldWithAlias() {
    HeavenPage page = page();
    page.clouds.shouldNot(exist);
    assertName("The clouds");
  }

  private static class HeavenPage {
    @FindBy(tagName = "select")
    @As("The clouds")
    @CacheLookup
    SelenideElement clouds;
  }

  private void assertName(String expectedName) {
    assertThat(report.events()).hasSize(1);
    assertThat(report.events().get(0).getElement()).isEqualTo(expectedName);
  }
}
