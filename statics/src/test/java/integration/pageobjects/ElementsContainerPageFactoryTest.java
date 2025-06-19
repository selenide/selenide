package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.container;
import static com.codeborne.selenide.Selenide.containers;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.tagName;

final class ElementsContainerPageFactoryTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_divs.html");
  }

  @Test
  void cainInitContainerByWebElement() {
    SelenideElement element = $("#parent_div");
    ParentDiv container = container(element, ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void cainInitContainerBySelenideWebElement() {
    SelenideElement element = $("#parent_div");
    ParentDiv container = $(element, ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerByCss() {
    ParentDiv container = container("#parent_div", ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerBySeleniumSelector() {
    ParentDiv container = container(tagName("div"), ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerBySelenideCss() {
    ParentDiv container = $("#parent_div", ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerBySelenideSeleniumSelector() {
    ParentDiv container = $(tagName("div"), ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerByXpath() {
    ParentDiv container = $x("//div", ParentDiv.class);
    container.self.shouldHave(ownText("Big papa"));
    container.child.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitNthContainerByCss() {
    ChildDiv container = container("#parent_div div", 1, ChildDiv.class);
    container.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerByNthSeleniumSelector() {
    ChildDiv container = container(cssSelector("#parent_div div"), 1, ChildDiv.class);
    container.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerByNthSelenideCss() {
    ChildDiv container = $("#parent_div div", 1, ChildDiv.class);
    container.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainerByNthSelenideSeleniumSelector() {
    ChildDiv container = $(cssSelector("#parent_div div"), 1, ChildDiv.class);
    container.self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersByWebElements() {
    ElementsCollection elements = $$("[id^='child_div']");
    elements.shouldHave(size(2));

    List<ChildDiv> containers = containers(elements.stream().toList(), ChildDiv.class);
    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersBySelenideWebElements() {
    ElementsCollection elements = $$("[id^='child_div']");
    elements.shouldHave(size(2));

    List<ChildDiv> containers = $$(elements.stream().toList(), ChildDiv.class);
    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  private static class ParentDiv implements Container {
    @Self
    SelenideElement self;

    @FindBy(css = "#child_div2")
    ChildDiv child;

    @FindBy(css = "div")
    List<Container> divs;
  }

  private static class ChildDiv implements Container {
    @Self
    SelenideElement self;
  }
}
