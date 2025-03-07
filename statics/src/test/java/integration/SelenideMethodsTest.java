package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.innerText;
import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.partialValue;
import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.tagName;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.actions;
import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.elements;
import static com.codeborne.selenide.Selenide.getFocusedElement;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.openqa.selenium.Keys.ALT;
import static org.openqa.selenium.Keys.CONTROL;
import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.Keys.TAB;

final class SelenideMethodsTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canOpenBlankPage() {
    open("about:blank");
  }

  @Test
  void userCanCheckIfElementIsDisplayed() {
    assertThat($(By.name("domain")).isDisplayed())
      .isTrue();
    assertThat($("#theHiddenElement").isDisplayed())
      .isFalse();
    assertThat($(By.name("non-existing-element")).isDisplayed())
      .isFalse();
  }

  @Test
  void userCanCheckIfElementExistsAndVisible() {
    assertThat($(By.name("domain")).isDisplayed())
      .isTrue();
    assertThat($("#theHiddenElement").isDisplayed())
      .isFalse();
    assertThat($(By.name("non-existing-element")).isDisplayed())
      .isFalse();

    $("#theHiddenElement").shouldBe(hidden);
    $("#theHiddenElement").should(disappear);
    $("#theHiddenElement").should(disappear, Duration.ofSeconds(2));
    $("#theHiddenElement").should(exist);

    $(".non-existing-element").should(Condition.not(exist));
    $(".non-existing-element").shouldNot(exist);
  }

  @Test
  void canGiveElementsHumanReadableNames() {
    assertThatThrownBy(() -> {
      $(By.xpath("/long/ugly/xpath[1][2][3]")).as("Login button").should(exist);
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element \"Login button\" not found {By.xpath: /long/ugly/xpath[1][2][3]}");
  }

  @Test
  void canGetElementAlias() {
    SelenideElement anonymousElement = $(By.xpath("/dev[1]/span[2]"));
    SelenideElement namedElement = $(By.xpath("/dev[1]/span[2]")).as("Login button");

    assertThat(anonymousElement.getAlias()).isNull();
    assertThat(namedElement.getAlias()).isEqualTo("Login button");
  }

  @Test
  void shouldMethodCanUseCustomTimeout() {
    $("#theHiddenElement").should(exist, Duration.ofNanos(3_000_000_000L));
    $("#theHiddenElement").shouldBe(hidden, ofMillis(3_000));
    $("#theHiddenElement").shouldHave(exactText(""), Duration.ofSeconds(3));
    $("#theHiddenElement").shouldNotHave(text("no"), Duration.ofHours(3));
  }

  @Test
  void userCanCheckIfElementIsReadonly() {
    $(By.name("username")).shouldBe(readonly);
    $(By.name("password")).shouldNotBe(readonly);
  }

  @Test
  void userCanGetInnerHtmlOfElement() {
    assertThat($(byValue("one.io")).innerHtml())
      .isEqualTo("@one.io");
    assertThat($(byText("@two.eu")).innerHtml())
      .isEqualTo("@two.eu");
    assertThat($(byText("@four.ee")).innerHtml())
      .isEqualTo("@four.ee");
    assertThat($("h2").innerHtml())
      .isEqualTo("Dropdown list");

    assertThat($("#baskerville").innerHtml().trim())
      .startsWith("<span></span> L'a  \n").endsWith("Baskerville");
    assertThat($("#status").innerHtml().trim())
      .isEqualTo("Username: <span class=\"name\">Bob Smith</span>&nbsp;Last login: <span class=\"last-login\">01.01.1970</span>");
  }

  @Test
  void userCanGetTextAndHtmlOfHiddenElement() {
    assertThat($("#theHiddenElement").innerHtml().trim())
      .isEqualToIgnoringCase("видишь суслика? и я не вижу. <b>а он есть</b>!");

    assertThat($("#theHiddenElement").innerText().trim())
      .isEqualTo("Видишь суслика? И я не вижу. А он есть!");

    $("#theHiddenElement").shouldHave(exactOwnText("Видишь суслика? И я не вижу. !"));
    $("#theHiddenElement").shouldHave(innerText("Видишь суслика? И я не вижу. А он есть!"));
  }

  @Test
  void innerText_errorMessage() {
    assertThatThrownBy(() -> $("#theHiddenElement").shouldHave(innerText("Видишь суслика?")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have inner text \"Видишь суслика?\" {#theHiddenElement}")
      .hasMessageContaining("Actual value: text=\"Видишь суслика? И я не вижу. А он есть!\"");
  }

  @Test
  void innerHtml_cannotBeNull() {
    assertThat($("br").innerHtml()).isEqualTo("");
    assertThatThrownBy(() -> $("#missing").innerHtml())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void innerText_cannotBeNull() {
    assertThat($("br").innerText()).isEqualTo("");
    assertThatThrownBy(() -> $("#missing").innerText())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void userCanPressEnter() {
    webdriver().shouldNotHave(urlContaining("#submitted-form"));
    $(By.name("password")).val("Going to press ENTER").pressEnter();
    webdriver().shouldHave(urlContaining("#submitted-form"), ofMillis(500));
  }

  @Test
  void userCanPressTab() {
    $("#username-blur-counter").shouldHave(text("___"));
    $("#username").sendKeys(" x ");
    $("#username").pressTab();

    $("#password").shouldBe(focused);
    $("#username-mirror").shouldHave(text("x (1)"));
    $("#username-blur-counter").shouldHave(text("blur: 1"));
  }

  @Test
  void userCanUnfocusElement() {
    $("#username-blur-counter").shouldHave(text("___"));
    $("#username").sendKeys(" x ");
    $("#username").unfocus();

    $("#username").shouldNotBe(focused);
    $("#password").shouldNotBe(focused);
    $("#username-mirror").shouldHave(text("x (1)"));
    $("#username-blur-counter").shouldHave(text("blur: 1"));
  }

  @Test
  void userCanPressAnyKeys() {
    $("#username")
      .press(" method $.press() is chainable ")
      .press(TAB, CONTROL, ALT, ENTER);
    $("#username-mirror").shouldHave(text(" method $.press() is chainable (1)"));
  }

  @Test
  void userCanCheckIfElementContainsText() {
    assertThat($("h1").text())
      .isEqualTo("Page with selects");
    assertThat($("h2").text())
      .isEqualTo("Dropdown list");
    assertThat($(By.name("domain")).find("option").text())
      .isEqualTo("@one.io");
    assertThat($("#radioButtons").text())
      .isEqualTo("Radio buttons\nМастер Маргарита Кот \"Бегемот\" Theodor Woland");

    $("h1").shouldHave(partialText("Page "));
    $("h2").shouldHave(text("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("@one.io"));
    $(By.name("domain")).find("option").shouldHave(partialText("ne.i"));
    $("#radioButtons").shouldHave(partialText("buttons"), partialText("Мастер"), partialText("Маргарита"));
  }

  @Test
  void userCanCheckIfElementHasExactText() {
    $("h1").shouldHave(exactText("Page with selects"));
    $("h2").shouldHave(exactText("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("@one.io"));
    $("#radioButtons").shouldHave(text("Radio buttons\n" +
                                       "Мастер Маргарита Кот \"Бегемот\" Theodor Woland"));
  }

  @Test
  void elementIsEmptyIfTextAndValueAreBothEmpty() {
    timeout = 4000;
    $("br").shouldBe(empty);
    $("h2").shouldNotBe(empty);
    $(By.name("password")).shouldBe(empty);
    $("#login").shouldNotBe(empty);
    $("#empty-text-area").shouldBe(empty);
    $("#text-area").shouldNotBe(empty);
  }

  @Test
  void canUseHaveWrapper() {
    $("#username-blur-counter").should(have(text("___")));
  }

  @Test
  void canUseHaveWrapper_errorMessage() {
    assertThatThrownBy(() -> $("#username-blur-counter").should(have(text("wrong-text"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should have text \"wrong-text\" {#username-blur-counter}")
      .hasMessageContaining("Actual value: text=\"___\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void canUseBeWrapper() {
    $("br").should(be(empty));
  }

  @Test
  void canUseBeWrapper_errorMessage() {
    assertThatThrownBy(() -> $("#username-blur-counter").should(be(disabled)))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should be disabled {#username-blur-counter}");
  }

  @Test
  void userCanGetOriginalWebElement() {
    WebElement selenideElement = $(By.name("domain")).toWebElement();
    WebElement seleniumElement = getWebDriver().findElement(By.name("domain"));

    assertThat(selenideElement.getClass()).isEqualTo(seleniumElement.getClass());
    assertThat(selenideElement.getTagName()).isEqualTo(seleniumElement.getTagName());
    assertThat(selenideElement.getText()).isEqualTo(seleniumElement.getText());
    assertThat(selenideElement.getAttribute("id")).isEqualTo(seleniumElement.getAttribute("id"));
    assertThat(selenideElement).isEqualTo(seleniumElement);
  }

  @Test
  void userCanCacheWebElement() {
    SelenideElement cachedElement = $(By.xpath("//select[@name='domain']")).cached();
    assertThat(cachedElement.getTagName()).isEqualTo("select");

    assertThatThrownBy(() -> cachedElement.shouldHave(text("WRONG TEXT")))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("Element should have text \"WRONG TEXT\" {By.xpath: //select[@name='domain']}");
  }

  @Test
  void userCanFollowLinks() {
    $(By.linkText("Want to see ajax in action?")).click();
    webdriver().shouldHave(urlContaining("long_ajax_request.html"), ofMillis(1000));
  }

  @Test
  void userCanUseSeleniumActions() {
    $(By.name("rememberMe")).shouldNotBe(selected);

    actions().click($(By.name("rememberMe"))).build().perform();

    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test
  void shouldNotThrowsElementNotFound() {
    assertThatThrownBy(() -> $(byText("Unexisting text")).shouldNotBe(hidden))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {by text: Unexisting text}");
  }

  @Test
  void shouldNotThrowsElementMatches() {
    assertThatThrownBy(() -> $(byText("Bob")).shouldNotHave(cssClass("firstname")))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageContaining("Element should not have css class \"firstname\" {by text: Bob}")
      .hasMessageContaining("Actual value: class=\"firstname\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void userCanCheckCssClass() {
    $(byText("Bob")).shouldHave(cssClass("firstname"));
    $(byText("Dilan")).shouldHave(cssClass("lastname"));
    $(byText("25")).shouldHave(cssClass("age"));
    $(byText("First name")).shouldNotHave(cssClass("anything"));
  }

  @Test
  void userCanCheckCssValue() {
    $(byId("theHiddenElement")).shouldHave(cssValue("display", "none"));
    $(byText("First name")).shouldNotHave(cssValue("font-size", "24"));
    $(byText("Last name")).shouldHave(cssValue("non-exist-prop", ""));
    $(byCssSelector("#status")).shouldHave(cssValue("line-height", "10px"));
  }

  @Test
  void userCanGetPageTitle() {
    assertThat(title())
      .isEqualTo("Test page :: with selects, but without JQuery");
  }

  @Test
  void userCanCheckElementId() {
    $("#multirowTable").shouldHave(id("multirowTable"));
    $("#login").shouldHave(id("login"));
    $(By.id("theHiddenElement")).shouldHave(id("theHiddenElement"));
    $("h3").shouldHave(id("username-mirror"));
  }

  @Test
  void userCanCheckElementName() {
    $("select").shouldHave(name("domain"));
    $(by("type", "radio")).shouldHave(name("me"));
    $(by("type", "checkbox")).shouldHave(name("rememberMe"));
    $("#username").shouldHave(name("username"));
  }

  @Test
  void userCanCheckElementType() {
    $("#login").shouldHave(type("submit"));
    $(By.name("me")).shouldHave(type("radio"));
    $(By.name("rememberMe")).shouldHave(type("checkbox"));
  }

  @Test
  void userCanFindFirstMatchingSubElement() {
    $(By.name("domain")).find("option").shouldHave(value("one.io"));
    $(By.name("domain")).$("option").shouldHave(value("one.io"));
  }

  @Test
  void findWaitsUntilParentAppears() {
    timeout = 4000;
    $("#container").find("#dynamic-content2").shouldBe(visible);
  }

  @Test
  void findWaitsUntilElementMatchesCondition() {
    timeout = 4000;
    $("#dynamic-content-container").find("#dynamic-content2").shouldBe(visible);
  }

  @Test
  void userCanListMatchingSubElements() {
    $("#multirowTable").findAll(byText("Chack")).shouldHave(size(2));
    $("#multirowTable").$$(byText("Chack")).shouldHave(size(2));
    $("#multirowTable tr").findAll(byText("Chack")).shouldHave(size(1));
    $("#multirowTable tr").$$(byText("Chack")).shouldHave(size(1));
  }

  @Test
  void errorMessageShouldContainUrlIfBrowserFailedToOpenPage() {
    try {
      baseUrl = "http://localhost:8080";
      open("www.duckduckgo.com");
      fail("expected WebDriverException");
    }
    catch (WebDriverException e) {
      assertThat(e.getAdditionalInformation())
        .contains("selenide.baseUrl: http://localhost:8080");
      assertThat(e.getAdditionalInformation())
        .contains("selenide.url: http://localhost:8080www.duckduckgo.com");
    }
  }

  @Test
  void userCanRightClickOnElement() {
    $(By.name("password")).contextClick();

    $("#login").contextClick().click();

    $(By.name("domain")).click();
    $(By.name("domain")).contextClick();
  }

  @Test
  void userCanCheckConditions() {
    assertThat($("#login").is(visible))
      .isTrue();
    assertThat($("#multirowTable").has(partialText("Chack")))
      .isTrue();

    assertThat($(".non-existing-element").has(text("Ninja")))
      .isFalse();
    assertThat($("#multirowTable").has(text("Ninja")))
      .isFalse();
  }

  @Test
  void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_1() {
    assertThatThrownBy(() -> $("h1").shouldHave(text("Some wrong test").because("it's wrong text")))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should have text \"Some wrong test\" (because it's wrong text) {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_2() {
    assertThatThrownBy(() -> $("h1").shouldHave(text("Some wrong test").because("it's wrong text")))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should have text \"Some wrong test\" (because it's wrong text) {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_3() {
    assertThatThrownBy(() -> $("h1").shouldHave(text("Some wrong test").because("it's wrong text")))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element should have text \"Some wrong test\" (because it's wrong text) {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void shouldNotMethodsMayContainOptionalMessageThatIsPartOfErrorMessage() {
    assertThatThrownBy(() -> $("h1").shouldNotHave(text("Page with selects").because("it's wrong text")))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not have text \"Page with selects\" (because it's wrong text) {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void waitWhileMethodMayContainOptionalMessageThatIsPartOfErrorMessage() {
    assertThatThrownBy(() -> {
      $("h1").shouldNotBe(visible.because("we expect it do disappear"), ofMillis(200));
    })
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not be visible (because we expect it do disappear) {h1}")
      .hasMessageContaining("Actual value: visible")
      .hasMessageContaining("Timeout: 200 ms.");
  }

  @Test
  void waitUntilMethodMayContainOptionalMessageThatIsPartOfErrorMessage() {
    assertThatThrownBy(() -> {
      $("h1").shouldBe(hidden.because("it's sensitive information"), ofMillis(100));
    })
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be hidden (because it's sensitive information) {h1}")
      .hasMessageContaining("Actual value: visible")
      .hasMessageContaining("Timeout: 100 ms.");
  }

  @Test
  void canUseElementAliases() {
    element("h1")
      .shouldBe(visible).shouldHave(text("Page with selects"));

    element(By.cssSelector("h1"))
      .shouldBe(visible).shouldHave(text("Page with selects"));

    element(getWebDriver().findElement(By.cssSelector("h1")))
      .shouldBe(visible).shouldHave(text("Page with selects"));

    element("h1", 0)
      .shouldBe(visible).shouldHave(text("Page with selects"));

    element("h1")
      .shouldBe(visible).shouldHave(text("Page with selects"));
  }

  @Test
  void canUseElementsAliases() {
    elements("[name='me']").shouldHave(size(4));
    elements(By.cssSelector("[name='me']")).shouldHave(size(4));
    elements(getWebDriver().findElements(By.cssSelector("[name='me']"))).shouldHave(size(4));
  }

  @Test
  void canExecuteJavascript() {
    Long value = Selenide.executeJavaScript("return 10;");
    assertThat(value).isEqualTo(10);
  }

  @Test
  void canExecuteAsyncJavascript() {
    Long value = Selenide.executeAsyncJavaScript(
      "var callback = arguments[arguments.length - 1]; setTimeout(function() { callback(10); }, 50);"
    );
    assertThat(value).isEqualTo(10);
  }

  @Test
  void toStringShowsLocator() {
    assertThat($("#theHiddenElement").toString())
      .isEqualTo("{#theHiddenElement}");

    assertThat($("#theHiddenElement").find(By.xpath(".//div[2]")).toString())
      .isEqualTo("{#theHiddenElement/By.xpath: .//div[2]}");

    assertThat($$("div").findBy(id("theHiddenElement")).toString())
      .isEqualTo("div.findBy(attribute id=\"theHiddenElement\")");
  }

  @Test
  void canCheckWhichElementIsFocusedNow() {
    SelenideElement focusedElement = getFocusedElement();

    $("#username").sendKeys("focusing...");
    focusedElement.shouldBe(visible).shouldHave(tagName("input"), partialValue("focusing"));
  }

  @Test
  void getFocusedElement_neverReturnsNull() {
    open("about:blank");
    getFocusedElement().shouldHave(tagName("body"));
  }
}
