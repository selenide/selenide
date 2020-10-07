package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PageObjectTest extends IntegrationTest {
  private SelectsPage pageWithSelects;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    pageWithSelects = page(SelectsPage.class);
  }

  @Test
  void userCanSelectOptionByValue() {
    pageWithSelects.selectDomain("myrambler.ru");

    assertThat(pageWithSelects.getSelectedOption().getAttribute("value"))
      .contains("myrambler.ru");
    assertThat(pageWithSelects.getSelectedOption().getText())
      .contains("@myrambler.ru");
  }

  @Test
  void userCanSelectOptionByText() {
    pageWithSelects.selectDomainByText("@мыло.ру");

    assertThat(pageWithSelects.getSelectedOption().getAttribute("value"))
      .contains("мыло.ру");
    assertThat(pageWithSelects.getSelectedOption().getText())
      .contains("@мыло.ру");
  }

  @Test
  void userCanInjectExistingPageObject() {
    SelectsPage originalPageObject = new SelectsPage();
    assertThat(originalPageObject.domainSelect)
      .isNull();

    SelectsPage pageObject = page(originalPageObject);
    assertThat(pageObject)
      .isEqualTo(originalPageObject);
    assertThat(pageObject.domainSelect)
      .isNotNull();
  }

  @Test
  void canInjectSelenideElement() {
    timeout = 4000;
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));
    pageWithSelects.dynamicContent.shouldHave(text("dynamic content"));
  }

  @Test
  void canInjectListOfSelenideElements() {
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));

    assertThat(pageWithSelects.h2s)
      .hasSize(3);
    pageWithSelects.h2s.get(0).shouldBe(visible).shouldHave(text("Dropdown list"));
    pageWithSelects.h2s.get(1).shouldBe(visible).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
    pageWithSelects.h2s.get(2).shouldBe(visible).shouldHave(text("Radio buttons"));
  }

  @Test
  void canInjectElementsCollection() {
    pageWithSelects.h1.shouldHave(Condition.text("Page with selects"));

    assertThat(pageWithSelects.h2sElementsCollection)
      .hasSize(3);
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
    assertThat(pageWithSelects.userInfoList)
      .hasSize(2);

    pageWithSelects.userInfoList.get(0).getSelf().shouldBe(visible);
    pageWithSelects.userInfoList.get(0).firstName.shouldHave(text("Bob"));

    pageWithSelects.userInfoList.get(1).lastName.shouldHave(text("Smith"));
    pageWithSelects.userInfoList.get(1).age.shouldHave(text("28"));
  }

  @Test
  void pageObjectShouldNotRequireElementExistenceAtCreation() {
    MissingSelectsPage page = page(MissingSelectsPage.class);
    assertThat(page.domainSelect.isDisplayed())
      .isFalse();
    assertThat(page.status.name.isDisplayed())
      .isFalse();
  }

  @Test
  void pageObjectShouldFailWhenTryingToOperateMissingElements() {
    MissingSelectsPage page = page(MissingSelectsPage.class);

    assertThatThrownBy(() -> page.domainSelect.click())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void pageObjectShouldFailWhenTryingToOperateElementsInMissingContainer() {
    MissingSelectsPage page = page(MissingSelectsPage.class);

    assertThatThrownBy(() -> page.status.lastLogin.click())
      .isInstanceOf(ElementNotFound.class);
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
