package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ElementsContainerWithManuallyInitializedFieldsTest extends IntegrationTest {

  @Before
  public void openTestPage () {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canInitializeElementsContainerFieldsWithoutFindByAnnotation () {
    MyPage page = page(MyPage.class);

    page.container.getSelf().should(exist, visible);
    page.container.headerLink.shouldHave(text("Options with 'apostrophes' and \"quotes\""));
    page.container.options.shouldHave(texts("-- Select your hero --", "John Mc'Lain", "Arnold \"Schwarzenegger\"", "Mickey \"Rock'n'Roll\" Rourke"));
  }


  private static class MyPage {
    @FindBy (css = "#apostrophes-and-quotes")
    MyContainer container;
  }

  private static class MyContainer extends ElementsContainer {
    SelenideElement headerLink = $("h2>a");
    ElementsCollection options = $$("#hero>option");
  }
}
