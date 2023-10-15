package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

final class SelenideElementToStringTest extends ITest {
  @Test
  void toStringMethod_showsSelector_withoutFetchingActualElementInformation() {
    assertThat($$("ul li").findBy(cssClass("nonexistent")))
      .hasToString("ul li.findBy(css class \"nonexistent\")");

    assertThat($$("ul li").findBy(cssClass("detective")))
      .hasToString("ul li.findBy(css class \"detective\")");
  }

  @Test
  void describe_showsElementDetails() {
    openFile("page_with_selects_without_jquery.html");

    assertThat($("h1").describe())
      .isEqualTo("<h1>Page with selects</h1>");
    assertThat($("h2").describe())
      .isEqualTo("<h2>Dropdown list</h2>");
    assertThat($(By.name("rememberMe")).describe())
      .isEqualTo("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>");

    assertThat($(By.name("domain")).find("option").describe())
      .isEqualTo("<option data-mailserverid=\"111\" value=\"one.io\" selected:true>@one.io</option>");

    assertThat($(byText("Want to see ajax in action?")).describe())
      .contains("<a href=");
    assertThat($(byText("Want to see ajax in action?")).describe())
      .contains(">Want to see ajax in action?</a>");
  }

  @Test
  void describe_showsAllAttributesButStyleSortedAlphabetically() {
    openFile("page_with_selects_without_jquery.html");

    assertThat($("#gopher").describe())
      .isEqualTo("<div class=\"invisible-with-multiple-attributes\" " +
        "data-animal-id=\"111\" id=\"gopher\" ng-class=\"widget\" ng-click=\"none\" " +
        "onchange=\"console.log(this);\" onclick=\"void(0);\" placeholder=\"Животное\" " +
        "displayed:false></div>");
  }

  @Test
  void describe_showsValueAttributeThatHasBeenUpdatedDynamically() {
    openFile("page_with_selects_without_jquery.html");

    $("#age").clear();
    $("#age").sendKeys("21");
    assertThat($("#age").describe())
      .isEqualTo("<input id=\"age\" name=\"age\" type=\"text\" value=\"21\"></input>");
  }

  @Test
  void describe_showsCurrentValue_evenIfItWasDynamicallyChanged() {
    openFile("page_with_double_clickable_button.html");
    assertThat($("#double-clickable-button").describe())
      .contains("value=\"double click me\"");

    $("#double-clickable-button").shouldBe(enabled).doubleClick();
    $("#double-clickable-button").shouldHave(value("do not click me anymore"));

    assertThat($("#double-clickable-button").describe()).contains("value=\"do not click me anymore\"");
    assertThat($("#double-clickable-button").toString()).contains("{#double-clickable-button}");
  }

  @Test
  void describe_forNonExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("nonexistent"));

    assertThat(element.toString()).isEqualTo("ul li.findBy(css class \"nonexistent\")");
    assertThat(element.describe())
      .startsWith("NoSuchElementException: Cannot locate an element ul li.findBy(css class \"nonexistent\")");
  }

}
