package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.tagName;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LazyPageObjectTest extends IntegrationTest {
  private MyPage myPage;

  @BeforeEach
  final void setUp() {
    closeWebDriver();
    myPage = page(MyPage.class);
    openFile("fortest.html");
  }

  @Test
  public void pageObject() {
    // level 2
    assertThat(myPage.myContent.forms).hasSize(1);

    // level 3
    MyForm subForm = myPage.myContent.forms.get(0);
    subForm.self.shouldBe(visible).shouldHave(tagName("form"));
    subForm.h1.shouldBe(visible);
    assertThat(subForm.h11.isDisplayed()).isTrue();
    subForm.h3s.shouldHave(size(0));
    assertThat(subForm.h3ss.size()).isEqualTo(0);
    assertThat(subForm.h3sss).hasSize(0);
    subForm.h2.shouldNot(exist);
    assertThat(subForm.h22.isDisplayed()).isFalse();

    // level 1
    myPage.h1.should(exist);
    myPage.h3.shouldNot(exist);
  }

  @Test
  public void shouldLoadElementsLazily() {
    myPage.wrongContent.h42.shouldNot(exist);
    myPage.h3.shouldNot(exist);
    List<MyForm> thisLineShouldNotFail = myPage.wrongContent.forms;

    assertThatThrownBy(thisLineShouldNotFail::size)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.className: wrong-content/form}")
      .hasMessageContaining("Timeout: 0 ms.")
      .cause()
      .isInstanceOf(NoSuchElementException.class);
  }

  static class MyPage {
    @FindBy(className = "content")
    private MyContent myContent;

    @FindBy(className = "wrong-content")
    private MyContent wrongContent;

    @FindBy(tagName = "h1")
    private SelenideElement h1;

    @FindBy(tagName = "h3")
    private SelenideElement h3;

    @FindBy(tagName = "h3")
    private List<WebElement> webElements;
  }

  static class MyContent implements Container {
    @FindBy(tagName = "form")
    private List<MyForm> forms;

    @FindBy(tagName = "h42")
    private SelenideElement h42;
  }

  static class MyForm implements Container {
    @FindBy(xpath = ".")
    private SelenideElement self;

    @FindBy(tagName = "h1")
    private SelenideElement h1;

    @FindBy(tagName = "h1")
    private WebElement h11;

    @FindBy(tagName = "h2")
    private SelenideElement h2;

    @FindBy(tagName = "h2")
    private WebElement h22;

    @FindBy(tagName = "h3")
    private ElementsCollection h3s;

    @FindBy(tagName = "h3")
    private ElementsCollection h3ss;

    @FindBy(tagName = "h3")
    private List<WebElement> h3sss;
  }
}
