package integration.pageobjects;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DynamicListOfWebElementsTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    timeout = 1000;
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void shouldWaitUntilCollectionIsLoaded() {
    sleep(500);
    MyPage page = page(MyPage.class);
    $(page.container.spans.get(0)).shouldHave(text("dynamic content"));
    $(page.container.spans.get(1)).shouldHave(text("dynamic content2"));

    assertThatThrownBy(() ->
      $(page.container.spans.get(2)).shouldHave(text("enough."))
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#dynamic-content-container/span[2]}");
  }

  private static class MyPage {
    @FindBy(css = "#dynamic-content-container")
    DynamicContentContainer container;
  }

  private static class DynamicContentContainer extends ElementsContainer {
    @FindBy(css = "span")
    List<WebElement> spans;
  }
}
