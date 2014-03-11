package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;
import static org.junit.Assert.*;

public class PageObjectTest extends IntegrationTest {

  private SelectsPage pageWithSelects;

  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
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
  public void canUseExtendedWebElementsFunctionality() {
    pageWithSelects.h1.shouldHave(Condition.text("Page without JQuery"));

    assertEquals(3, pageWithSelects.h2s.size());
    pageWithSelects.h2s.get(0).shouldBe(visible).shouldHave(text("Dropdown list"));
    pageWithSelects.h2s.get(1).shouldBe(visible).shouldHave(text("Options with apostrophes and quotes"));
    pageWithSelects.h2s.get(2).shouldBe(visible).shouldHave(text("Radio buttons"));

    pageWithSelects.dynamicContent.shouldHave(text("dynamic content"));
  }

  @Test
  public void canComposePageFromReusableBlocks() {
    pageWithSelects.status.getSelf().shouldBe(visible);
    pageWithSelects.status.name.shouldHave(text("Bob Smith"));
    pageWithSelects.status.lastLogin.shouldHave(text("01.01.1970"));
  }

  @Test
  public void canComposePageFromListOfReusableBlocks() {
    assertEquals(2, pageWithSelects.userInfoList.size());

    pageWithSelects.userInfoList.get(0).getSelf().shouldBe(visible);
    pageWithSelects.userInfoList.get(0).firstName.shouldHave(text("Bob"));

    pageWithSelects.userInfoList.get(1).lastName.shouldHave(text("Smith"));
    pageWithSelects.userInfoList.get(1).age.shouldHave(text("28"));
  }

  public static class SelectsPage {
    @FindBy(xpath = "//select[@name='domain']")
    public WebElement domainSelect;

    @FindBy(tagName = "h1")
    public SelenideElement h1;

    @FindBy(tagName = "h2")
    public List<SelenideElement> h2s;

    @FindBy(id = "dynamic-content")
    public SelenideElement dynamicContent;

    @FindBy(id = "status")
    public StatusBlock status;

    @FindBy(css = "#user-table tbody tr")
    public List<UserInfo> userInfoList;

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

  public static class StatusBlock extends ElementsContainer {
    @FindBy(className = "name")
    SelenideElement name;

    @FindBy(className = "last-login")
    SelenideElement lastLogin;
  }

  public static class UserInfo extends ElementsContainer {
    @FindBy(className = "firstname")
    SelenideElement firstName;
    @FindBy(className = "lastname")
    SelenideElement lastName;
    @FindBy(className = "age")
    SelenideElement age;
  }
}
