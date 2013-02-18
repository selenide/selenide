package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.Selenide.$;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;

public class PageWithJQuery {
  @Before
  public void openTestPageWithJQuery() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_jquery.html"));
  }

  @Test
  public void worksForPagesWithJQuery() {
    assertTrue(DOM.isJQueryAvailable());
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    if (WebDriverRunner.htmlUnit()) {
      DOM.triggerChangeEvent(By.id("username"));
    }
    $("h2").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");
    if (WebDriverRunner.htmlUnit()) {
      DOM.triggerChangeEvent(By.id("username"));
    }

    $("h2").shouldHave(text("john bon-jovi"));
  }
}
