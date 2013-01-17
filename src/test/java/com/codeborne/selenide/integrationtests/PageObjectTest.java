package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ShouldableWebElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DOM.page;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.Navigation.sleep;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

public class PageObjectTest {

  private SelectsPage pageWithSelects;

  @Before
  public void openTestPage() {
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
    sleep(100);
    pageWithSelects = page(SelectsPage.class);
  }

  @Test
  public void userCanSelectOptionByValue() {
    pageWithSelects.selectDomain("myrambler.ru");

    assertEquals("myrambler.ru", pageWithSelects.getSelectedOption().getAttribute("value"));
    assertEquals("@myrambler.ru", pageWithSelects.getSelectedOption().getText());
  }

  @Test
  public void userCanSelectOptionByText() {
    pageWithSelects.selectDomainByText("@мыло.ру");
    assertEquals("мыло.ру", pageWithSelects.getSelectedOption().getAttribute("value"));
    assertEquals("@мыло.ру", pageWithSelects.getSelectedOption().getText());
  }

  @Test
  public void userCanInjectExistingPageObject() {
    SelectsPage originalPageObject = new SelectsPage();
    assertNull(originalPageObject.domainSelect);

    SelectsPage pageObject = page(originalPageObject);
    assertSame(originalPageObject, pageObject);
    assertNotNull(pageObject.domainSelect);
  }

  @Test
  public void canUseExtendedWebElementsFunctionality() throws Exception {
    pageWithSelects.h1.shouldHave(Condition.text("Page without JQuery"));

    assertEquals(2, pageWithSelects.h2s.size());
    pageWithSelects.h2s.get(0).shouldBe(visible).shouldHave(text("Dropdown list"));
    pageWithSelects.h2s.get(1).shouldBe(visible).shouldHave(text("Radio buttons"));

    pageWithSelects.dynamicContent.shouldHave(text("dynamic content"));
  }

  public static class SelectsPage {
    @FindBy(xpath = "//select[@name='domain']")
    public WebElement domainSelect;

    @FindBy(tagName = "h1")
    public ShouldableWebElement h1;

    @FindBy(tagName = "h2")
    public List<ShouldableWebElement> h2s;

    @FindBy(id = "dynamic-content")
    public ShouldableWebElement dynamicContent;

    public WebElement getSelectedOption() {
      return new Select(domainSelect).getFirstSelectedOption();
    }

    public void selectDomain(String domainValue) {
      new Select(domainSelect).selectByValue(domainValue);
      sleep(500);
    }

    public void selectDomainByText(String domainValue) {
      new Select(domainSelect).selectByVisibleText(domainValue);
      sleep(500);
    }
  }
}
