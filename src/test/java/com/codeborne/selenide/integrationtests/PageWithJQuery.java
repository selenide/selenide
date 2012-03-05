package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.Navigation;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PageWithJQuery {
  @Test
  public void worksForPagesWithJQuery() {
    Navigation.navigateToAbsoluteUrl("http://jquery.com/"); // TODO Use something like localhost:8080/with-jquery
    assertTrue(DOM.isJQueryAvailable());
    // TODO Test all methods on this page
  }
}
