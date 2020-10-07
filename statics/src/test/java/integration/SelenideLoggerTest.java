package integration;

import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.logevents.EventsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SelenideLoggerTest extends IntegrationTest {
  private static final String LISTENER = "SelenideLoggerTest";
  private final EventsCollector collector = new EventsCollector();

  @BeforeEach
  void setUp() {
    openFile("page_with_selects_without_jquery.html");
    SelenideLogger.addListener(LISTENER, collector);
  }

  @AfterEach
  void tearDown() {
    SelenideLogger.removeListener(LISTENER);
  }

  @Test
  void val() {
    $(By.name("password")).val("sherlyn");

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"By.name: password\") val(sherlyn)");
    assertThat(collector.events().get(0).getElement()).isEqualTo("By.name: password");
    assertThat(collector.events().get(0).getStatus()).isEqualTo(PASS);
    assertThat(collector.events().get(0).getSubject()).isEqualTo("val(sherlyn)");
    assertThat(collector.events().get(0).getError()).isNull();
    assertThat(collector.events().get(0).getDuration()).isBetween(0L, 10_000L);
  }

  @Test
  void shouldHaveText() {
    assertThatThrownBy(() -> $("h1").shouldHave(exactText("A wrong header")))
      .isInstanceOf(ElementShould.class);

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"h1\") should have(exact text 'A wrong header')");
  }

  @Test
  void shouldHaveSize() {
    $$("h1").shouldHave(size(2));

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"h1\") should have(size(2))");
  }

  @Test
  void filterShouldHaveSize() {
    $$("h1").filterBy(visible).shouldHave(size(2));

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"h1.filter(visible)\") should have(size(2))");
  }

  @Test
  void findAllShouldHaveSize() {
    $("body").findAll("h1").shouldHave(sizeGreaterThan(0));

    assertThat(collector.events()).hasSize(2);
    assertThat(collector.events().get(0)).hasToString("$(\"body\") find all(h1)");
    assertThat(collector.events().get(1)).hasToString("$(\"body/h1\") should have(size > 0)");
  }

  @Test
  void findElements() {
    assertThat($("body").findElements(By.tagName("h1")).size()).isEqualTo(2);

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"body\") find elements(By.tagName: h1)");
  }

  @Test
  void collectionByXpath() {
    $$x("//h1").shouldHave(size(2));

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"By.xpath: //h1\") should have(size(2))");
  }

  @Test
  void collectionByXpathGetText() {
    assertThat($$x("//h1").first().getText()).isEqualTo("Page with selects");

    assertThat(collector.events()).hasSize(1);
    assertThat(collector.events().get(0)).hasToString("$(\"By.xpath: //h1[0]\") get text()");
  }
}
