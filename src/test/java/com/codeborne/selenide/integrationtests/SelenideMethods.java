package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
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
    assertFalse($(By.name("non-existing-element")).exists());
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
    assertEquals("<option value=livemail.ru checked=true selected:true>@livemail.ru</option>",
        $(By.name("domain")).find("option").toString());
  }
}
