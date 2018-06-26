package integration;

import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;

class PageObjectTest extends IntegrationTest {

  private SelectsPage pageWithSelects;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    sleep(100);
    pageWithSelects = page(SelectsPage.class);
  }

  @Test
  void userCanSelectOptionByValue() {
    pageWithSelects.selectDomain("myrambler.ru");

    Assertions.assertEquals("myrambler.ru", pageWithSelects.getSelectedOption().getAttribute("value"));
    Assertions.assertEquals("@myrambler.ru", pageWithSelects.getSelectedOption().getText());
  }

  @Test
  void userCanSelectOptionByText() {
    pageWithSelects.selectDomainByText("@мыло.ру");
    Assertions.assertEquals("мыло.ру", pageWithSelects.getSelectedOption().getAttribute("value"));
    Assertions.assertEquals("@мыло.ру", pageWithSelects.getSelectedOption().getText());
  }

  @Test
  void userCanInjectExistingPageObject() {
    SelectsPage originalPageObject = new SelectsPage();
    Assertions.assertNull(originalPageObject.domainSelect);

    SelectsPage pageObject = page(originalPageObject);
    Assertions.assertSame(originalPageObject, pageObject);
    Assertions.assertNotNull(pageObject.domainSelect);
  }

  @Test
  void canInjectSelenideElement() {
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));
    pageWithSelects.dynamicContent.shouldHave(text("dynamic content"));
  }

  @Test
  void canInjectListOfSelenideElements() {
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));

    Assertions.assertEquals(3, pageWithSelects.h2s.size());
    pageWithSelects.h2s.get(0).shouldBe(visible).shouldHave(text("Dropdown list"));
    pageWithSelects.h2s.get(1).shouldBe(visible).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
    pageWithSelects.h2s.get(2).shouldBe(visible).shouldHave(text("Radio buttons"));
  }

  @Test
  void canInjectElementsCollection() {
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));

    Assertions.assertEquals(3, pageWithSelects.h2sElementsCollection.size());
    pageWithSelects.h2sElementsCollection.get(0)
      .shouldBe(visible)
      .shouldHave(text("Dropdown list"));

    pageWithSelects.h2sElementsCollection.get(1)
      .shouldBe(visible)
      .shouldHave(text("Options with 'apostrophes' and \"quotes\""));

    pageWithSelects.h2sElementsCollection.get(2)
      .shouldBe(visible)
      .shouldHave(text("Radio buttons"));
  }

  @Test
  void canComposePageFromReusableBlocks() {
    pageWithSelects.status.getSelf().shouldBe(visible);
    pageWithSelects.status.name.shouldHave(text("Bob Smith"));
    pageWithSelects.status.lastLogin.shouldHave(text("01.01.1970"));
  }

  @Test
  void canComposePageFromListOfReusableBlocks() {
    Assertions.assertEquals(2, pageWithSelects.userInfoList.size());

    pageWithSelects.userInfoList.get(0).getSelf().shouldBe(visible);
    pageWithSelects.userInfoList.get(0).firstName.shouldHave(text("Bob"));

    pageWithSelects.userInfoList.get(1).lastName.shouldHave(text("Smith"));
    pageWithSelects.userInfoList.get(1).age.shouldHave(text("28"));
  }

  @Test
  void pageObjectShouldNotRequireElementExistenceAtCreation() {
    MissingSelectsPage page = page(MissingSelectsPage.class);
    Assertions.assertFalse(page.domainSelect.isDisplayed());
    Assertions.assertFalse(page.status.name.isDisplayed());
  }

  @Test
  void pageObjectShouldFailWhenTryingToOperateMissingElements() {
    MissingSelectsPage page = page(MissingSelectsPage.class);

    Assertions.assertThrows(ElementNotFound.class,
      () -> page.domainSelect.click());
  }

  @Test
  void pageObjectShouldFailWhenTryingToOperateElementsInMissingContainer() {
    MissingSelectsPage page = page(MissingSelectsPage.class);

    Assertions.assertThrows(ElementNotFound.class,
      () -> page.status.lastLogin.click());
  }

  private static class SelectsPage {
    @FindBy(xpath = "//select[@name='domain']")
    WebElement domainSelect;

    @FindBy(tagName = "h1")
    SelenideElement h1;

    @FindBy(tagName = "h2")
    List<SelenideElement> h2s;

    @FindBy(tagName = "h2")
    ElementsCollection h2sElementsCollection;

    @FindBy(id = "dynamic-content")
    SelenideElement dynamicContent;

    @FindBy(id = "status")
    StatusBlock status;

    @FindBy(css = "#user-table tbody tr")
    List<UserInfo> userInfoList;

    WebElement getSelectedOption() {
      return new Select(domainSelect).getFirstSelectedOption();
    }

    void selectDomain(String domainValue) {
      $(domainSelect).selectOptionByValue(domainValue);
    }

    void selectDomainByText(String domainValue) {
      $(domainSelect).selectOption(domainValue);
    }
  }

  static class StatusBlock extends ElementsContainer {
    @FindBy(className = "name")
    SelenideElement name;

    @FindBy(className = "last-login")
    SelenideElement lastLogin;
  }

  static class UserInfo extends ElementsContainer {
    @FindBy(className = "firstname")
    SelenideElement firstName;
    @FindBy(className = "lastname")
    SelenideElement lastName;
    @FindBy(className = "age")
    SelenideElement age;
  }

  static class MissingSelectsPage {
    @FindBy(xpath = "//select[@name='wrong-select-name']")
    WebElement domainSelect;
    @FindBy(id = "wrong-id")
    StatusBlock status;

    private MissingSelectsPage() {
    }
  }
}
