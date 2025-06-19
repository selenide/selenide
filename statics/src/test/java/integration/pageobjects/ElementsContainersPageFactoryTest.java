package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ElementsContainersPageFactoryTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_divs.html");
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

  @Test
  void canInitContainersByCss() {
    List<ChildDiv> containers = containers("[id^='child_div']", ChildDiv.class);
    assertThat(containers).hasSize(2);

    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersBySelenideCss() {
    List<ChildDiv> containers = $$("[id^='child_div']", ChildDiv.class);
    assertThat(containers).hasSize(2);

    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersBySeleniumSelector() {
    List<ChildDiv> containers = containers(By.cssSelector("[id^='child_div']"), ChildDiv.class);
    assertThat(containers).hasSize(2);

    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersBySelenideSeleniumSelector() {
    List<ChildDiv> containers = $$(By.cssSelector("[id^='child_div']"), ChildDiv.class);
    assertThat(containers).hasSize(2);

    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  @Test
  void canInitContainersByXpath() {
    List<ChildDiv> containers = $$x("//*[starts-with(@id, 'child_div')]", ChildDiv.class);
    assertThat(containers).hasSize(2);

    containers.get(0).self.shouldHave(ownText("Son"));
    containers.get(1).self.shouldHave(ownText("Daughter"));
  }

  private static class ChildDiv implements Container {
    @Self
    SelenideElement self;
  }
}
