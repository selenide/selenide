package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

final class SelenidePageFactoryTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("start_page.html");
  }

  @Test
  void canInitPageWithFindByAnnotations() {
    StartPageWithAnnotation startPage = page(StartPageWithAnnotation.class);
    startPage.pageText.shouldHave(text("Start page"));
  }

  @Test
  void canInitPageWithoutFindByAnnotations() {
    StartPageWithoutAnnotation startPage = page(StartPageWithoutAnnotation.class);
    startPage.pageHeader.shouldHave(text("Selenide"));
  }
}

class StartPageWithAnnotation {
  @FindBy(css = "#start-selenide")
  SelenideElement pageText;
}

class StartPageWithoutAnnotation {
  SelenideElement pageHeader = $("h1");
}
