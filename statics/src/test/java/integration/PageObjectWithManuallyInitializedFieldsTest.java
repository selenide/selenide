package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;

final class PageObjectWithManuallyInitializedFieldsTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canInitializePageObjectWithoutFindByAnnotation() {
    MyPage page = page(MyPage.class);

    page.h1.shouldHave(Condition.text("Page with selects"));
    assertThat(page.h2s).hasSize(3);
    page.h2s.get(0).shouldBe(visible).shouldHave(text("Dropdown list"));
    page.h2First.shouldBe(visible).shouldHave(text("Dropdown list"));
  }

  private static class MyPage {
    SelenideElement h1 = $("h1");
    List<SelenideElement> h2s = $$("h2");
    SelenideElement h2First = $$("h2").get(0);
  }
}
