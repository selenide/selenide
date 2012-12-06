package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DOM;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;

public class PageWithJQuery {
  @Before
  public void openTestPageWithJQuery() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("long_ajax_request.html"));
  }

  @Test
  public void worksForPagesWithJQuery() {
    assertTrue(DOM.isJQueryAvailable());
  }
}
