package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.JQuery;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;

public class PageWithJQuery {
  JQuery jQuery = new JQuery();

  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_jquery.html"));
  }

  @Test
  public void worksForPagesWithJQuery() {
    assertTrue(jQuery.isJQueryAvailable());
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    $("h2").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("h2").shouldHave(text("john bon-jovi"));
  }
}
