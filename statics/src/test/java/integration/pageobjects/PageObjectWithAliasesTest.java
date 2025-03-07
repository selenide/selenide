package integration.pageobjects;

import com.codeborne.selenide.As;
import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PageObjectWithAliasesTest extends IntegrationTest {

  private PageObject page;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    page = page(PageObject.class);
  }

  @Test
  void fieldWithAlias_shouldHave() {
    assertThatThrownBy(() ->
      $(page.header3).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
          Element "Tiny header" should have text "expected text" {h6}
          """.trim()
      );
  }

  @Test
  void fieldWithAlias_shouldNotHave() {
    assertThatThrownBy(() ->
      $(page.header1).shouldNotHave(text("Page with selects"))
    )
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("""
          Element "Large header" should not have text "Page with selects" {h1}
          """.trim()
      );
  }

  @Test
  void selfWithAlias() {
    assertThatThrownBy(() -> $(page.header.self).shouldHave(text("Nope")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
          Element "The header" should have text "Nope" {h1}
          """.trim()
      );
  }

  @Test
  void collectionWithAlias() {
    assertThatThrownBy(() -> page.header2.shouldHave(size(666))).isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("""
        List size mismatch: expected: = 666, actual: 4, collection: Middle headers {h2}
        """.trim());
  }

  @Test
  void collectionWithAlias_singleElement() {
    assertThatThrownBy(() -> page.header2.get(2).shouldHave(text("Nope"))).isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
        Element should have text "Nope" {h2[2]}
        """.trim());
  }

  @Test
  void fieldWithDollarAndAlias() {
    assertThatThrownBy(() ->
      $(page.header4).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("""
          Element "Dollar header" should have text "expected text" {h1}
          """.trim()
      );
  }

  @Test
  void collectionWithDollarAndAlias() {
    assertThatThrownBy(() ->
      page.header5.shouldHave(size(666))
    )
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("""
          List size mismatch: expected: = 666, actual: 4, collection: Dollar collection {h2}
          """.trim()
      );
  }

  static class PageObject {
    @As("The header")
    @FindBy(tagName = "h1")
    Header header;

    @As("Large header")
    @FindBy(tagName = "h1")
    SelenideElement header1;

    @As("Middle headers")
    @FindBy(tagName = "h2")
    ElementsCollection header2;

    @As("Tiny header")
    @FindBy(tagName = "h6")
    WebElement header3;

    @As("Dollar header")
    SelenideElement header4 = $("h1");

    @As("Dollar collection")
    ElementsCollection header5 = $$("h2");
  }

  static class Header implements Container {
    @Self
    SelenideElement self;
  }
}
