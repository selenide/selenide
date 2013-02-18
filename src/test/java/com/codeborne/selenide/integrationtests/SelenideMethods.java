package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.DOM.$$;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.Navigation.url;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

public class SelenideMethods {
  @Before
  public void openTestPageWithJQuery() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
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
  public void userCanSetValueToTextfield() {
    $(By.name("password")).setValue("john");
    $(By.name("password")).val("sherlyn");
    $(By.name("password")).shouldHave(value("sherlyn"));
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
  public void userCanCheckElementText() {
    assertEquals("Page without JQuery", $("h1").text());
    assertEquals("Dropdown list", $("h2").text());
    assertEquals("@livemail.ru", $(By.name("domain")).find("option").text());
    assertEquals("Radio buttons\nЯ идиот Я тупица Я готов I don't speak Russian", $("#radioButtons").text());
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
    $(By.name("rememberMe")).shouldNotBe(checked);

    $(By.name("rememberMe")).click();

    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);
    assertEquals("<input name=rememberMe value=on type=checkbox selected:true></input>",
        $(By.name("rememberMe")).toString());
  }

  @Test
  public void userCanCheckCssClass() {
    $(byText("Bob")).shouldHave(cssClass("firstname"));
    $(byText("Dilan")).shouldHave(cssClass("lastname"));
    $(byText("25")).shouldHave(cssClass("age"));
    $(byText("First name")).shouldNotHave(cssClass("anything"));
  }
}
