package integration.pageobjects;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FieldOfGenericTypeTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void injectsFoundSelenideElementAsSelf() {
    DummyPage page = page(DummyPage.class);
    assertThat(page).isInstanceOf(DummyPage.class);
    assertThat(page.body).isInstanceOf(DummyTypedElement.class);
    assertThat(page.body.names).isNull();
    assertThat(page.body.selects).isInstanceOf(List.class);
    assertThat(page.body.selects).hasSize(4);
    assertThat(page.body.getSelf()).isEqualTo($("body"));
    assertThat(page.body.selects.get(0)).isEqualTo($("select[name=domain]"));
    assertThat(page.body.selects.get(1)).isEqualTo($("select#hero"));
    assertThat(page.body.selects.get(2)).isEqualTo($("select#gender"));
    assertThat(page.body.selects.get(3)).isEqualTo($("select#cars"));
  }

  @Test
  void injectsFoundSelenideElementAsSelf2() {
    AnotherPage page = page(AnotherPage.class);
    assertThat(page.body.selects).hasSize(4);
    assertThat(page.body).isInstanceOf(ElementsContainer.class);
    assertThat(page.body.getSelf()).isEqualTo($("body"));
    assertThat(page.body.selects.get(0)).isInstanceOf(WebElement.class);
    assertThat(page.body.selects.get(0)).isEqualTo($("select[name=domain]"));
    assertThat(page.body.selects.get(1)).isEqualTo($("select#hero"));
    assertThat(page.body.selects.get(2)).isEqualTo($("select#gender"));
    assertThat(page.body.selects.get(3)).isEqualTo($("select#cars"));
  }

  @Test
  void injectsFoundSelenideElementAsSelf3() {
    YetAnotherPage page = page(YetAnotherPage.class);
    assertThat(page.body).isNotNull();
    assertThat(page.body.selects).hasSize(4);

    assertThatThrownBy(() -> page.body.selects.get(0))
      .isInstanceOf(RuntimeException.class)
      .hasMessageMatching("Cannot initialize field .*List .*DummyTypedElement.selects: .*ElementsContainer is abstract");
  }

  static class DummyTypedElement<S, T> extends ElementsContainer {
    private List<S> names;

    @FindBy(tagName = "select")
    private List<T> selects;
  }

  static class DummyPage {
    @FindBy(tagName = "body")
    private DummyTypedElement<String, SelenideElement> body;
  }

  static class AnotherPage {
    @FindBy(tagName = "body")
    private DummyTypedElement<Integer, WebElement> body;
  }

  static class YetAnotherPage {
    @FindBy(tagName = "body")
    DummyTypedElement<Boolean, ElementsContainer> body;
  }
}
