package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Container.ShadowRoot;
import com.codeborne.selenide.DeepShadow;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ShadowHost;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;

public class PageObjectWithShadowElementsTest extends IntegrationTest {

  private PageObject pageObject;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
    pageObject = page();
    pageObject.header.shouldBe(visible, Duration.ofMillis(300));
  }

  @Test
  void elementWithShadowHost() {
    pageObject.input.setValue("I can type text inside of shadow dom");
    pageObject.input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void elementWithDeepShadow() {
    pageObject.deepInput.setValue("I can type text inside of shadow dom");
    pageObject.deepInput.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void innerElementWithShadowHost() {
    pageObject.innerInput.setValue("I can type text inside of shadow dom");
    pageObject.innerInput.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void innerElementWithDeepShadow() {
    pageObject.deepInnerInput.setValue("I can type text inside of shadow dom");
    pageObject.deepInnerInput.shouldHave(exactValue("I can type text inside of shadow dom"));
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
  void elementsListWithDeepShadow() {
    List<SelenideElement> elementsList = pageObject.deepEelementsList;
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
  void innerElementsListWithShadowHost() {
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
  void innerElementsListWithDeepShadow() {
    List<SelenideElement> elementsList = pageObject.deepInnerElementsList;
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
  void innerContainerWithShadowRoot() {
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
  void containerWithDeepShadowAndShadowRoot() {
    ShadowItem item = pageObject.deepInnerShadowContainer.item;
    assertThat(item.p.text()).isEqualTo("shadowContainerChildHost1");
  }

  @Test
  void innerContainerWithShadowHostAndShadowRoot() {
    ShadowInnerItem shadowInnerItem = pageObject.innerInnerShadowContainer.item;
    assertThat(shadowInnerItem.self.text()).isEqualTo("shadowContainerChildChild1Host1");
  }

  @Test
  void innerContainerWithDeepShadowAndShadowRoot() {
    ShadowInnerItem shadowInnerItem = pageObject.deepInnerInnerShadowContainer.item;
    assertThat(shadowInnerItem.self.text()).isEqualTo("shadowContainerChildChild1Host1");
  }

  @Test
  void elementsFromDifferentShadowRoots() {
    List<SelenideElement> elements = pageObject.elementsFromDifferentShadowRoots;
    assertThat(elements).as("Mismatch in the size of expected elements").hasSize(6);

    String text = elements.stream()
      .map(SelenideElement::text)
      .collect(Collectors.joining(System.lineSeparator()));

    assertThat(text).isEqualTo("""
      shadowContainerChildChild1Host1
      shadowContainerChildChild2Host1
      shadowContainerChildChild1Host1
      shadowContainerChildChild2Host1
      shadowContainerChildChild1Host3
      shadowContainerChildChild1Host3""");
  }

  private static class PageObject {

    @FindBy(tagName = "h1")
    SelenideElement header;

    @ShadowHost(@FindBy(id = "shadow-host"))
    @FindBy(css = "#inputInShadow")
    SelenideElement input;

    @DeepShadow
    @FindBy(css = "#inputInShadow")
    SelenideElement deepInput;

    @ShadowHost({@FindBy(id = "shadow-host"), @FindBy(css = "#inner-shadow-host")})
    @FindBy(css = "#inputInInnerShadow")
    SelenideElement innerInput;

    @DeepShadow
    @FindBy(css = "#inputInInnerShadow")
    SelenideElement deepInnerInput;

    @ShadowHost(@FindBy(id = "shadow-host"))
    @FindAll({@FindBy(css = "#inputInShadow"), @FindBy(css = "#buttonInShadow")})
    List<SelenideElement> elementsList;

    @DeepShadow
    @FindAll({@FindBy(css = "#inputInShadow"), @FindBy(css = "#buttonInShadow")})
    List<SelenideElement> deepEelementsList;

    @ShadowHost({@FindBy(xpath = "//*[@id='shadow-host']"), @FindBy(css = "#inner-shadow-host")})
    @FindAll({@FindBy(css = "#inputInInnerShadow"), @FindBy(css = "#buttonInInnerShadow")})
    List<SelenideElement> innerElementsList;

    @DeepShadow
    @FindAll({@FindBy(css = "#inputInInnerShadow"), @FindBy(css = "#buttonInInnerShadow")})
    List<SelenideElement> deepInnerElementsList;

    @ShadowRoot
    @FindBy(css = "#shadow-container")
    ShadowContainer shadowContainer;

    @ShadowRoot
    @ShadowHost(@FindBy(id = "shadow-container"))
    @FindBy(css = ".shadow-container-child")
    ShadowChild innerShadowContainer;

    @ShadowRoot
    @DeepShadow
    @FindBy(css = ".shadow-container-child")
    ShadowChild deepInnerShadowContainer;

    @ShadowHost({@FindBy(id = "shadow-container"), @FindBy(css = ".shadow-container-child")})
    @FindBy(css = ".shadow-container-child-child")
    ShadowInnerChild innerInnerShadowContainer;

    @DeepShadow
    @FindBy(css = ".shadow-container-child-child")
    ShadowInnerChild deepInnerInnerShadowContainer;

    @DeepShadow
    @FindBy(css = "[class^=shadow-container-child-child]")
    List<SelenideElement> elementsFromDifferentShadowRoots;
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
