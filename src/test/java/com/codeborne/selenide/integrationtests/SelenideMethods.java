package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.junit.ScreenShooter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

public class SelenideMethods {
  @Rule
  public ScreenShooter allScreens = ScreenShooter.failedTests();

  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void userCanCheckIfElementExists() {
    assertTrue($(By.name("domain")).exists());
    assertTrue($("#theHiddenElement").exists());
    assertFalse($(By.name("non-existing-element")).exists());
  }

  @Test
  public void userCanCheckIfElementExistsAndVisible() {
    assertTrue($(By.name("domain")).isDisplayed());
    assertFalse($("#theHiddenElement").isDisplayed());
    assertFalse($(By.name("non-existing-element")).isDisplayed());

    $("#theHiddenElement").shouldBe(hidden);
    $("#theHiddenElement").should(disappear);
    $("#theHiddenElement").waitUntil(disappears, 1000);
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

    assertTrue($(By.name("rememberMe")).toString().contains("<input name=rememberMe"));
    assertTrue($(By.name("rememberMe")).toString().contains("type=checkbox></input>"));

    assertEquals("<option value=livemail.ru selected:true>@livemail.ru</option>",
        $(By.name("domain")).find("option").toString());

    assertTrue($(byText("Want to see ajax in action?")).toString().contains("<a href="));
    assertTrue($(byText("Want to see ajax in action?")).toString().contains(">Want to see ajax in action?</a>"));
  }

  @Test
  public void userCanFindElementByAttribute() {
    assertEquals("meta", $(byAttribute("http-equiv", "Content-Type")).getTagName());
    assertEquals("select", $(byAttribute("name", "domain")).getTagName());
    assertEquals("@мыло.ру", $(byAttribute("value", "мыло.ру")).getText());
    assertEquals("div", $(byAttribute("id", "radioButtons")).getTagName());
    assertEquals(4, $$(byAttribute("type", "radio")).size());
    assertEquals("username", $(byAttribute("readonly", "readonly")).getAttribute("name"));
  }

  @Test
  public void userCanGetDataAttributes() {
    assertEquals("111", $(byValue("livemail.ru")).getAttribute("data-mailServerId"));
    assertEquals("111", $(byValue("livemail.ru")).data("mailServerId"));

    assertEquals("222A", $(byText("@myrambler.ru")).data("mailServerId"));
    assertEquals("33333B", $(byValue("rusmail.ru")).data("mailServerId"));
    assertEquals("111АБВГД", $(byText("@мыло.ру")).data("mailServerId"));
  }

  @Test @Ignore
  public void userCanSearchElementByDataAttribute() {
    assertEquals("111АБВГД", $(by("data-mailServerId", "111АБВГД")).data("mailServerId"));
  }

  @Test
  public void userCanSetValueToTextfield() {
    $(By.name("password")).setValue("john");
    $(By.name("password")).val("sherlyn");
    $(By.name("password")).shouldBe(focused);
    $(By.name("password")).shouldHave(value("sherlyn"));
    $(By.name("password")).waitUntil(hasValue("sherlyn"), 1000);
    assertEquals("sherlyn", $(By.name("password")).val());
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
  public void userCanCheckIfElementContainsText() {
    assertEquals("Page without JQuery", $("h1").text());
    assertEquals("Dropdown list", $("h2").text());
    assertEquals("@livemail.ru", $(By.name("domain")).find("option").text());
    assertEquals("Radio buttons\nЯ идиот Я тупица Я готов I don't speak Russian", $("#radioButtons").text());

    $("h1").shouldHave(text("Page "));
    $("h2").shouldHave(text("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("vemail.r"));
    $("#radioButtons").shouldHave(text("buttons\nЯ идиот Я тупица"));
  }

  @Test
  public void userCanCheckIfElementHasExactText() {
    $("h1").shouldHave(exactText("Page without JQuery"));
    $("h2").shouldHave(exactText("Dropdown list"));
    $(By.name("domain")).find("option").shouldHave(text("@livemail.ru"));
    $("#radioButtons").shouldHave(text("Radio buttons\n" +
        "Я идиот Я тупица Я готов I don't speak Russian"));
  }

  @Test
  public void elementIsEmptyIfTextAndValueAreBothEmpty() {
    $("br").shouldBe(empty);
    $("h2").shouldNotBe(empty);
    $(By.name("password")).shouldBe(empty);
    $("#login").shouldNotBe(empty);
  }

  @Test @Ignore
  public void userCanUploadFiles() {
    $("#file_upload").uploadFromClasspath("some-file.txt");
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
    assertTrue(url().endsWith("long_ajax_request.html"));
  }

  @Test
  public void userCanSelectCheckbox() {
    $(By.name("rememberMe")).shouldNotBe(selected);

    $(By.name("rememberMe")).click();

    $(By.name("rememberMe")).shouldBe(selected);
    assertEquals("<input name=rememberMe value=on type=checkbox selected:true></input>",
        $(By.name("rememberMe")).toString());
  }

  @Test
  public void userCanUseSeleniumActions() {
    $(By.name("rememberMe")).shouldNotBe(selected);

    actions().click($(By.name("rememberMe"))).build().perform();

    $(By.name("rememberMe")).shouldBe(selected);
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
  public void userCanListMatchingSubElements() {
    $("#multirowTable").findAll(byText("Chack")).shouldHaveSize(2);
    $("#multirowTable").$$(byText("Chack")).shouldHaveSize(2);
    $("#multirowTable tr").findAll(byText("Chack")).shouldHaveSize(1);
    $("#multirowTable tr").$$(byText("Chack")).shouldHaveSize(1);
  }
}
