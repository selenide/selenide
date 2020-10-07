package integration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

final class SelenideElementToStringTest extends ITest {
  @Test
  void toStringMethodShowsElementDetails() {
    openFile("page_with_selects_without_jquery.html");

    assertThat($("h1"))
      .hasToString("<h1>Page with selects</h1>");
    assertThat($("h2"))
      .hasToString("<h2>Dropdown list</h2>");
    assertThat($(By.name("rememberMe")))
      .hasToString("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>");

    assertThat($(By.name("domain")).find("option"))
      .hasToString("<option data-mailserverid=\"111\" value=\"livemail.ru\" selected:true>@livemail.ru</option>");

    assertThat($(byText("Want to see ajax in action?")).toString())
      .contains("<a href=");
    assertThat($(byText("Want to see ajax in action?")).toString())
      .contains(">Want to see ajax in action?</a>");
  }

  @Test
  void toStringShowsAllAttributesButStyleSortedAlphabetically() {
    openFile("page_with_selects_without_jquery.html");

    assertThat($("#gopher"))
      .hasToString("<div class=\"invisible-with-multiple-attributes\" " +
        "data-animal-id=\"111\" id=\"gopher\" ng-class=\"widget\" ng-click=\"none\" " +
        "onchange=\"console.log(this);\" onclick=\"void(0);\" placeholder=\"Животное\" " +
        "displayed:false></div>");
  }

  @Test
  void toStringShowsValueAttributeThatHasBeenUpdatedDynamically() {
    openFile("page_with_selects_without_jquery.html");

    $("#age").clear();
    $("#age").sendKeys("21");
    assertThat($("#age"))
      .hasToString("<input id=\"age\" name=\"age\" type=\"text\" value=\"21\"></input>");
  }

  @Test
  void toStringShowsCurrentValue_evenIfItWasDynamicallyChanged() {
    openFile("page_with_double_clickable_button.html");
    assertThat($("#double-clickable-button").toString())
      .contains("value=\"double click me\"");

    $("#double-clickable-button").shouldBe(enabled).doubleClick();
    $("#double-clickable-button").shouldHave(value("do not click me anymore"));

    assertThat($("#double-clickable-button").toString())
      .contains("value=\"do not click me anymore\"");
  }
}
