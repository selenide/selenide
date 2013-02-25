package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.JQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertFalse;

public class PageWithoutJQuery {
  JQuery jQuery = new JQuery();

  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void worksForPagesWithoutJQuery() {
    assertFalse(jQuery.isJQueryAvailable());
  }

  @Test @Ignore // TODO Trigger onchange event without jQuery as described here:
  // - http://stackoverflow.com/questions/136617/how-do-i-programatically-force-an-onchange-event-on-an-input
  // - http://stackoverflow.com/questions/2856513/trigger-onchange-event-manually
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("john bon-jovi"));
  }
}
