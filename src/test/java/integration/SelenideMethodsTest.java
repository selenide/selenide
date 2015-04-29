package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.startsWith;

public class SelenideMethodsTest extends IntegrationTest {

  private final String additionalMessage = "test message";
  private final String becauseAdditionMessage = "because test message";

  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void userCanCheckIfElementExists() {
    assertTrue($(By.name("domain")).exists());
    assertTrue($("#theHiddenElement").exists());
    assertFalse($(By.name("non-existing-element")).exists());
  }

  @Test
  public void userCanCheckIfElementIsDisplayed() {
    assertTrue($(By.name("domain")).isDisplayed());
    assertFalse($("#theHiddenElement").isDisplayed());
    assertFalse($(By.name("non-existing-element")).isDisplayed());
  }

  @Test
  public void userCanCheckIfElementExistsAndVisible() {
    assertTrue($(By.name("domain")).isDisplayed());
    assertFalse($("#theHiddenElement").isDisplayed());
    assertFalse($(By.name("non-existing-element")).isDisplayed());

    $("#theHiddenElement").shouldBe(hidden);
    $("#theHiddenElement").should(disappear);
    $("#theHiddenElement").waitUntil(disappears, 1000);
    $("#theHiddenElement").should(exist);
    $("#theHiddenElement").shouldBe(present);
    $("#theHiddenElement").waitUntil(present, 1000);

    $(".non-existing-element").should(not(exist));
    $(".non-existing-element").shouldNot(exist);
    $(".non-existing-element").shouldNotBe(present);
    $(".non-existing-element").waitUntil(not(present), 1000);
    $(".non-existing-element").waitWhile(present, 1000);
  }

  @Test
  public void userCanCheckIfElementIsReadonly() {
    $(By.name("username")).shouldBe(readonly);
    $(By.name("password")).shouldNotBe(readonly);
  }

  @Test
  public void toStringMethodShowsElementDetails() {
    assertEquals("<h1>Page without JQuery</h1>", $("h1").toString());
    assertEquals("<h2>Dropdown list</h2>", $("h2").toString());

    if (isHtmlUnit()) {
      assertEquals("<input name=\"rememberMe\" value=\"on\" type=\"checkbox\"></input>", 
          $(By.name("rememberMe")).toString());
      
      assertEquals("<option value=\"livemail.ru\" selected:true>@livemail.ru</option>",
              $(By.name("domain")).find("option").toString());
      
    } else {
      assertEquals("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>", 
          $(By.name("rememberMe")).toString());

      assertEquals("<option data-mailserverid=\"111\" value=\"livemail.ru\" selected:true>@livemail.ru</option>",
              $(By.name("domain")).find("option").toString());
    }

    assertTrue($(byText("Want to see ajax in action?")).toString().contains("<a href="));
    assertTrue($(byText("Want to see ajax in action?")).toString().contains(">Want to see ajax in action?</a>"));
  }

  @Test
  public void toStringShowsAllAttributesButStyleSortedAlphabetically() {
    assumeFalse(isHtmlUnit());
    assertEquals("<div class=\"invisible-with-multiple-attributes\" " +
        "data-animal-id=\"111\" id=\"gopher\" ng-class=\"widget\" ng-click=\"none\" " +
        "onchange=\"console.log(this);\" onclick=\"void(0);\" placeholder=\"Животное\" " +
        "displayed:false></div>", $("#gopher").toString());
  }

  @Test
  public void toStringShowsOnlySomeAttributesInHtmlunit() {
    assumeTrue(isHtmlUnit());
    assertEquals("<div id=\"gopher\" class=\"invisible-with-multiple-attributes\" " +
        "placeholder=\"Животное\" onclick=\"void(0);\" onchange=\"console.log(this);\" " +
        "displayed:false></div>", $("#gopher").toString());
  }

  @Test
  public void userCanFindElementByAttribute() {
    assertEquals("select", $(byAttribute("name", "domain")).getTagName());
    assertEquals("@мыло.ру", $(byAttribute("value", "мыло.ру")).getText());
    assertEquals("div", $(byAttribute("id", "radioButtons")).getTagName());
    assertEquals(4, $$(byAttribute("type", "radio")).size());
    assertEquals("username", $(byAttribute("readonly", "readonly")).getAttribute("name"));
    assertEquals("meta", $(byAttribute("http-equiv", "Content-Type")).getTagName());
  }

  @Test
  public void userCanGetAttr() {
    assertEquals("username", $(by("readonly", "readonly")).attr("name"));
  }

  @Test
  public void userCanGetNameAttribute() {
    assertEquals("username", $(by("readonly", "readonly")).name());
  }

  @Test
  public void userCanGetDataAttributes() {
    assertEquals("111", $(byValue("livemail.ru")).getAttribute("data-mailServerId"));
    assertEquals("111", $(byValue("livemail.ru")).data("mailServerId"));

    assertEquals("222A", $(byText("@myrambler.ru")).data("mailServerId"));
    assertEquals("33333B", $(byValue("rusmail.ru")).data("mailServerId"));
    assertEquals("111АБВГД", $(byText("@мыло.ру")).data("mailServerId"));
  }

  @Test
  public void userCanGetInnerHtmlOfElement() {
    assertEquals("@livemail.ru", $(byValue("livemail.ru")).innerHtml());
    assertEquals("@myrambler.ru", $(byText("@myrambler.ru")).innerHtml());
    assertEquals("@мыло.ру", $(byText("@мыло.ру")).innerHtml());
    assertEquals("Dropdown list", $("h2").innerHtml());

    if (isHtmlUnit()) {
      assertEquals("<span></span> l'a\n      baskerville", $("#baskerville").innerHtml().trim().toLowerCase());
      assertEquals("username: <span class=name>bob smith</span> last login: <span class=last-login>01.01.1970</span>",
          $("#status").innerHtml().trim().toLowerCase());
    }
    else {
      assertEquals("<span></span> L'a\n            Baskerville", $("#baskerville").innerHtml().trim());
      assertEquals("Username: <span class=\"name\">Bob Smith</span>&nbsp;Last login: <span class=\"last-login\">01.01.1970</span>",
          $("#status").innerHtml().trim());
    }
  }

  @Test
  public void userCanGetTextAndHtmlOfHiddenElement() {
    assertEquals("видишь суслика? и я не вижу. <b>а он есть</b>!",
        $("#theHiddenElement").innerHtml().trim().toLowerCase());

    assertEquals("Видишь суслика? И я не вижу. А он есть!",
        $("#theHiddenElement").innerText().trim());
  }

  @Test
  public void userCanSearchElementByDataAttribute() {
    assumeFalse(isChrome() || isHtmlUnit() || isPhantomjs());

    assertEquals("111", $(by("data-mailServerId", "111")).data("mailServerId"));
    assertEquals("222A", $(by("data-mailServerId", "222A")).data("mailServerId"));
    assertEquals("33333B", $(by("data-mailServerId", "33333B")).data("mailServerId"));
    assertEquals("111АБВГД", $(by("data-mailServerId", "111АБВГД")).data("mailServerId"));
  }

  @Test
  public void userCanSearchElementByTitleAttribute() {
    assertEquals("fieldset", $(byTitle("Login form")).getTagName());
  }

  @Test
  public void userCanSetValueToTextfield() {
    $(By.name("password")).setValue("john");
    $(By.name("password")).val("sherlyn");
//    $(By.name("password")).shouldBe(focused);
    $(By.name("password")).shouldHave(value("sherlyn"));
    $(By.name("password")).waitUntil(hasValue("sherlyn"), 1000);
    assertEquals("sherlyn", $(By.name("password")).val());
  }

  @Test
  public void valueCheckIgnoresDifferenceInInvisibleCharacters() {
    $(By.name("password")).setValue("john   \u00a0 Malkovich");
    $(By.name("password")).shouldHave(value("john Malkovich"));

    $("#text-area").setValue("john   \u00a0 \r\nMalkovich\n");
    $("#text-area").shouldHave(value("john Malkovich"));
  }

  @Test
  public void userCanAppendValueToTextfield() {
    $(By.name("password")).val("Sherlyn");
    $(By.name("password")).append(" theron");
    $(By.name("password")).shouldHave(value("Sherlyn theron"));
    assertEquals("Sherlyn theron", $(By.name("password")).val());
  }

  @Test
  public void userCanPressEnter() {
    assertEquals(-1, url().indexOf("#submitted-form"));
    $(By.name("password")).val("Going to press ENTER").pressEnter();
    assertTrue(url().contains("#submitted-form"));
  }

  @Test
  public void userCanPressTab() {
    $("#username-blur-counter").shouldHave(text("___"));
    $("#username").sendKeys(" x ");
    $("#username").pressTab();

    if (!isHtmlUnit()) {
      // fails in HtmlUnit for unknown reason
      $("#password").shouldBe(focused);
      $("#username-mirror").shouldHave(text(" x "));
      $("#username-blur-counter").shouldHave(text("blur: "));
    }
  }

  @Test
  public void userCanCheckIfElementContainsText() {
    assertEquals("Page without JQuery", $("h1").text());
    assertEquals("Dropdown list", $("h2").text());
    assertEquals("@livemail.ru", $(By.name("domain")).find("option").text());
    assertEquals("Radio buttons\nМастер Маргарита Кот \"Бегемот\" Theodor Woland", $("#radioButtons").text());

    $("h1").shouldHave(text("Page "));
    $("h2").shouldHave(text("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("vemail.r"));
    $("#radioButtons").shouldHave(text("buttons\nМастер Маргарита"));
  }

  @Test
  public void userCanCheckIfElementHasExactText() {
    $("h1").shouldHave(exactText("Page without JQuery"));
    $("h2").shouldHave(exactText("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("@livemail.ru"));
    $("#radioButtons").shouldHave(text("Radio buttons\n" +
        "Мастер Маргарита Кот \"Бегемот\" Theodor Woland"));
  }

  @Test
  public void elementIsEmptyIfTextAndValueAreBothEmpty() {
    $("br").shouldBe(empty);
    $("h2").shouldNotBe(empty);
    $(By.name("password")).shouldBe(empty);
    $("#login").shouldNotBe(empty);
    $("#empty-text-area").shouldBe(empty);
    $("#text-area").shouldNotBe(empty);
  }

  @Test
  public void canUseHaveWrapper() {
    $("#username-blur-counter").should(have(text("___")));
  }

  @Test
  public void canUseHaveWrapper_errorMessage() {
    thrown.expect(ElementShould.class);
    thrown.expectMessage(startsWith("Element should have text 'wrong-text' {#username-blur-counter}"));
    
    $("#username-blur-counter").should(have(text("wrong-text")));
  }

  @Test
  public void canUseBeWrapper() {
    $("br").should(be(empty));
  }

  @Test
  public void canUseBeWrapper_errorMessage() {
    thrown.expect(ElementShould.class);
    thrown.expectMessage(startsWith("Element should be disabled {#username-blur-counter}"));
    
    $("#username-blur-counter").should(be(disabled));
  }
  
  @Test
  public void userCanGetOriginalWebElement() {
    WebElement selenideElement = $(By.name("domain")).toWebElement();
    WebElement seleniumElement = getWebDriver().findElement(By.name("domain"));

    assertSame(seleniumElement.getClass(), selenideElement.getClass());
    assertEquals(seleniumElement.getTagName(), selenideElement.getTagName());
    assertEquals(seleniumElement.getText(), selenideElement.getText());
  }

  @Test
  public void userCanFollowLinks() {
    $(By.linkText("Want to see ajax in action?")).followLink();
//    $(By.linkText("Want to see ajax in action?")).click();
    assertTrue("Actual URL is: " + url(), url().contains("long_ajax_request.html"));
  }
  
  @Test
  public void userCanUseSeleniumActions() {
    $(By.name("rememberMe")).shouldNotBe(selected);

    actions().click($(By.name("rememberMe"))).build().perform();

    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test
  public void shouldNotThrowsElementNotFound() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {by text: Unexisting text}");
    
    $(byText("Unexisting text")).shouldNotBe(hidden);
  }

  @Test 
  public void shouldNotThrowsElementMatches() {
    thrown.expect(ElementShouldNot.class);
    thrown.expectMessage("Element should not have css class 'firstname' {by text: Bob}");
    
    $(byText("Bob")).shouldNotHave(cssClass("firstname"));
  }

  @Test
  public void userCanCheckCssClass() {
    $(byText("Bob")).shouldHave(cssClass("firstname"));
    $(byText("Dilan")).shouldHave(cssClass("lastname"));
    $(byText("25")).shouldHave(cssClass("age"));
    $(byText("First name")).shouldNotHave(cssClass("anything"));
  }

  @Test
  public void userCanGetPageTitle() {
    assertEquals("long ajax request", title());
  }

  @Test
  public void userCanCheckElementId() {
    $("#multirowTable").shouldHave(id("multirowTable"));
    $("#login").shouldHave(id("login"));
    $(By.id("theHiddenElement")).shouldHave(id("theHiddenElement"));
    $("h3").shouldHave(id("username-mirror"));
  }

  @Test
  public void userCanCheckElementName() {
    $("select").shouldHave(name("domain"));
    $(by("type", "radio")).shouldHave(name("me"));
    $(by("type", "checkbox")).shouldHave(name("rememberMe"));
    $("#username").shouldHave(name("username"));
  }

  @Test
  public void userCanCheckElementType() {
    $("#login").shouldHave(type("submit"));
    $(By.name("me")).shouldHave(type("radio"));
    $(By.name("rememberMe")).shouldHave(type("checkbox"));
  }

  @Test
  public void userCanFindFirstMatchingSubElement() {
    $(By.name("domain")).find("option").shouldHave(value("livemail.ru"));
    $(By.name("domain")).$("option").shouldHave(value("livemail.ru"));
  }

  @Test
  public void findWaitsUntilParentAppears() {
    $("#container").find("#dynamic-content2").shouldBe(visible);
  }

  @Test
  public void findWaitsUntilElementMatchesCondition() {
    $("#dynamic-content-container").find("#dynamic-content2").shouldBe(visible);
  }

  @Test
  public void userCanListMatchingSubElements() {
    $("#multirowTable").findAll(byText("Chack")).shouldHaveSize(2);
    $("#multirowTable").$$(byText("Chack")).shouldHaveSize(2);
    $("#multirowTable tr").findAll(byText("Chack")).shouldHaveSize(1);
    $("#multirowTable tr").$$(byText("Chack")).shouldHaveSize(1);
  }

  @Test
  public void errorMessageShouldContainUrlIfBrowserFailedToOpenPage() {
    try {
      baseUrl = "http://localhost:8080";
      open("www.yandex.ru");
    } catch (WebDriverException e) {
      assertTrue(e.getAdditionalInformation().contains("selenide.baseUrl: http://localhost:8080"));
      assertTrue(e.getAdditionalInformation().contains("selenide.url: http://localhost:8080www.yandex.ru"));
    }
  }

  @Test
  public void userCanRightClickOnElement() {
    $(By.name("password")).contextClick();

    $("#login").contextClick().click();

    $(By.name("domain")).find("option").click();
    $(By.name("domain")).find("option").contextClick();
  }

  @Test
  public void userCanDoubleClickOnElement() {
    openFile("page_with_jquery.html");
    
    $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);
    
    $("#double-clickable-button")
        .doubleClick()
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);
    
    $("h2").shouldHave(text("Double click worked"));
  }

  @Test
  public void userCanCheckConditions() {
    assertTrue($("#login").is(visible));
    assertTrue($("#multirowTable").has(text("Chack")));

    assertFalse($(".non-existing-element").has(text("Ninja")));
    assertFalse($("#multirowTable").has(text("Ninja")));
  }

  @Test(expected = InvalidSelectorException.class)
  public void checkFailsForInvalidSelector() {
    $(By.xpath("//input[:attr='al]")).is(visible);
  }
  
  @Test
  public void userCanUseOrCondition() {
    Condition one_of_conditions = or("baskerville", text("Basker"), text("Walle"));
    $("#baskerville").shouldBe(one_of_conditions);

    Condition all_of_conditions = or("baskerville", text("Basker"), text("rville"));
    $("#baskerville").shouldBe(all_of_conditions);

    Condition none_of_conditions = or("baskerville", text("pasker"), text("wille"));
    $("#baskerville").shouldNotBe(none_of_conditions);
  }

  @Test
  public void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_1() {
    timeout = 100L;
    thrown.expect(ElementShould.class);
    thrown.expectMessage(contains(becauseAdditionMessage));

    $("h1").should("test message", text("Some wrong test"));
  }

  @Test
  public void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_2() {
    timeout = 100L;
    thrown.expect(ElementShould.class);
    thrown.expectMessage(contains(becauseAdditionMessage));

    $("h1").shouldHave("test message", text("Some wrong test"));
  }

  @Test
  public void shouldMethodsMayContainOptionalMessageThatIsPartOfErrorMessage_3() {
    timeout = 100L;
    thrown.expect(ElementShould.class);
    thrown.expectMessage(contains(becauseAdditionMessage));

    $("h1").shouldBe("test message", text("Some wrong test"));
  }

  @Test
  public void shouldNotMethodsMayContainOptionalMessageThatIsPartOfErrorMessage() {
    timeout = 100L;
    thrown.expect(ElementShouldNot.class);
    thrown.expectMessage(contains(becauseAdditionMessage));
    $("h1").shouldNot(additionalMessage, text("Page without JQuery"));

    try {
      $("h1").shouldNotHave(additionalMessage, text("Page without JQuery"));
      fail("exception expected");
    } catch (ElementShouldNot expected) {
      assertTrue(expected.getMessage().contains(becauseAdditionMessage));
    }

    try {
      $("h1").shouldNotBe(additionalMessage, text("Page without JQuery"));
      fail("exception expected");
    } catch (ElementShouldNot expected) {
      assertTrue(expected.getMessage().contains(becauseAdditionMessage));
    }
  }

  @Test
  public void waitWhileMethodMayContainOptionalMessageThatIsPartOfErrorMessage() {
    try {
      $("h1").waitWhile("test message", visible, 100);
      fail("exception expected");
    } catch (ElementShouldNot expected){
      assertTrue(expected.getMessage().contains(becauseAdditionMessage));
    }
  }

  @Test
  public void waitUntilMethodMayContainOptionalMessageThatIsPartOfErrorMessage() {
    try {
      $("h1").waitUntil("test message", hidden, 100);
      fail("exception expected");
    } catch (ElementShould expected){
      assertTrue(expected.getMessage().contains(becauseAdditionMessage));
    }
  }

  @Test
  public void canZoomInAndOut() {
    assumeFalse(isPhantomjs());
    int initialX = $(By.name("domain")).getLocation().getX();
    
    zoom(1.1);
    assertBetween($(By.name("domain")).getLocation().getY(), 140, 160);
    assertEquals(initialX, $(By.name("domain")).getLocation().getX());
    
    zoom(2.0);
    assertBetween($(By.name("domain")).getLocation().getY(), 240, 260);
    assertEquals(initialX, $(By.name("domain")).getLocation().getX());

    zoom(0.5);
    assertBetween($(By.name("domain")).getLocation().getY(), 70, 80);
    assertEquals(initialX, $(By.name("domain")).getLocation().getX());
  }
  
  private static void assertBetween(int n, int lower, int upper) {
    if (!isHtmlUnit()) {
      assertTrue(n + " should be between " + lower + " and " + upper, n >= lower);
      assertTrue(n + " should be between " + lower + " and " + upper, n <= upper);
    }
  }
}
