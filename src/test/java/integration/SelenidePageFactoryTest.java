package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by sepi on 21.12.16.
 */
public class SelenidePageFactoryTest extends IntegrationTest {

  @Before
  public void openTestPage() {
    openFile("start_page.html");
  }

  @Test
  public void canInitPageWithFindByAnnotations() {
    StartPageWithAnnotation startPage = page(StartPageWithAnnotation.class);
    startPage.pageText.shouldHave(Condition.text("Start page"));
  }

  @Test
  public void canInitPageWithoutFindByAnnotations() {
    StartPageWithoutAnnotation startPage = page(StartPageWithoutAnnotation.class);
    startPage.pageHeader.shouldHave(Condition.text("Selenide"));
  }
}

class StartPageWithAnnotation {
  @FindBy(css = "#start-selenide")
  public SelenideElement pageText;
}

class StartPageWithoutAnnotation {
  public SelenideElement pageHeader = $("h1");
}


