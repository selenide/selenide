package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Container.ShadowRoot;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ShadowHost;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;

public class PageObjectWithShadowElementsTest extends IntegrationTest {

  private PageObject pageObject;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
    pageObject = page(PageObjectWithShadowElementsTest.PageObject.class);
  }

  @Test
  void elementWithShadowHost() {
    pageObject.input.setValue("I can type text inside of shadow dom");
    pageObject.input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void elementWithInnerShadowHost() {
    pageObject.innerInput.setValue("I can type text inside of shadow dom");
    pageObject.innerInput.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void elementsListWithShadowHost() {
    List<SelenideElement> elementsList = pageObject.elementsList;
    assertThat(elementsList).as("Mismatch in the size of expected elements").hasSize(2);

    SelenideElement input = elementsList.get(0);
    input.setValue("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));

    SelenideElement button = elementsList.get(1);
    button.shouldHave(exactText("Button 1"));
    button.click();
    button.shouldHave(exactText("Changed Button 1"));
  }

  @Test
  void elementsListWithInnerShadowHost() {
    List<SelenideElement> elementsList = pageObject.innerElementsList;
    assertThat(elementsList).as("Mismatch in the size of expected elements").hasSize(2);

    SelenideElement input = elementsList.get(0);
    input.setValue("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));

    SelenideElement button = elementsList.get(1);
    button.shouldHave(exactText("Button 2"));
    button.click();
    button.shouldHave(exactText("Changed Button 2"));
  }

  @Test
  void containerWithShadowRoot() {
    List<ShadowChild> children = pageObject.shadowContainer.children;
    assertThat(children).isNotEmpty();

    String text = children.stream()
      .map(it -> it.item.self.text())
      .collect(Collectors.joining(System.lineSeparator()));

    assertThat(text).isEqualTo("""
                               shadowContainerChildHost1
                               shadowContainerChildHost2
                               shadowContainerChildHost3""");
  }

  @Test
  void containerWithInnerShadowRoot() {
    List<ShadowChild> children = pageObject.shadowContainer.children;
    assertThat(children.get(0).item.p.text()).isEqualTo("shadowContainerChildHost1");
    assertThat(children.get(1).item.p.text()).isEqualTo("shadowContainerChildHost2");
    assertThat(children.get(2).item.p.text()).isEqualTo("shadowContainerChildHost3");
  }

  @Test
  void containerWithShadowHostAndShadowRoot() {
    ShadowItem item = pageObject.innerShadowContainer.item;
    assertThat(item.p.text()).isEqualTo("shadowContainerChildHost1");
  }

  @Test
  void containerWithInnerShadowHostAndShadowRoot() {
    ShadowInnerItem shadowInnerItem = pageObject.innerShadowChild.item;
    assertThat(shadowInnerItem.self.text()).isEqualTo("shadowContainerChildChild1Host1");
  }

  private static class PageObject {

    @ShadowHost(@FindBy(id = "shadow-host"))
    @FindBy(css = "#inputInShadow")
    SelenideElement input;

    @ShadowHost({@FindBy(id = "shadow-host"), @FindBy(css = "#inner-shadow-host")})
    @FindBy(css = "#inputInInnerShadow")
    SelenideElement innerInput;

    @ShadowHost(@FindBy(id = "shadow-host"))
    @FindAll({@FindBy(css = "#inputInShadow"), @FindBy(css = "#buttonInShadow")})
    List<SelenideElement> elementsList;

    @ShadowHost({@FindBy(xpath = "//*[@id='shadow-host']"), @FindBy(css = "#inner-shadow-host")})
    @FindAll({@FindBy(css = "#inputInInnerShadow"), @FindBy(css = "#buttonInInnerShadow")})
    List<SelenideElement> innerElementsList;

    @ShadowRoot
    @FindBy(css = "#shadow-container")
    ShadowContainer shadowContainer;

    @ShadowRoot
    @ShadowHost(@FindBy(id = "shadow-container"))
    @FindBy(css = ".shadow-container-child")
    ShadowChild innerShadowContainer;

    @ShadowHost({@FindBy(id = "shadow-container"), @FindBy(css = ".shadow-container-child")})
    @FindBy(css = ".shadow-container-child-child")
    ShadowInnerChild innerShadowChild;
  }

  private static class ShadowContainer implements Container {

    @Self
    SelenideElement self;

    @ShadowRoot
    @FindBy(css = ".shadow-container-child")
    List<ShadowChild> children;
  }

  private static class ShadowChild implements Container {

    @FindBy(css = ".shadow-container-child-item")
    ShadowItem item;

    @FindBy(css = ".shadow-container-child-child")
    List<ShadowInnerChild> children;
  }

  private static class ShadowItem implements Container {

    @Self
    SelenideElement self;

    @FindBy(css = "p")
    SelenideElement p;
  }

  @ShadowRoot
  private static class ShadowInnerChild implements Container {

    @FindBy(css = ".shadow-container-child-child-item")
    ShadowInnerItem item;
  }

  private static class ShadowInnerItem implements Container {

    @Self
    SelenideElement self;
  }
}
