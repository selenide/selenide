package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DOM;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;

public class PageWithJQuery {
  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_jquery.html"));
  }

  @Test
  public void worksForPagesWithJQuery() {
    assertTrue(DOM.isJQueryAvailable());
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    executeJavaScript("$('#username').change()");
    $("h2").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");
    executeJavaScript("$('#username').change()");

    $("h2").shouldHave(text("john bon-jovi"));
  }
}
