package integration.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
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
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LazyPageObjectTest extends IntegrationTest {
  @BeforeEach
  final void setUp() {
    openFile("fortest.html");
  }

  @Test
  public void level1() {
    MyForm subForm = page(MyForm.class);
    subForm.h1.should(exist);
    assertThat(subForm.h11.isDisplayed()).isTrue();
    subForm.h3s.shouldHave(size(0));
    assertThat(subForm.h3ss).hasSize(0);
    assertThat(subForm.h3sss).hasSize(0);
    subForm.h2.shouldNot(exist);
    assertThat(subForm.h22.isDisplayed()).isFalse();
  }

  @Test
  public void level2() {
    MyContent myContent = page(MyContent.class);
    assertThat(myContent.forms).hasSize(1);
    myContent.forms.get(0).h1.shouldBe(visible);
  }

  @Test
  public void level3() {
    MyPage myPage = page(MyPage.class);
    assertThat(myPage.myContent.forms).hasSize(1);
    myPage.h1.should(exist);
    myPage.h3.shouldNot(exist);
  }

  @Test
  public void shouldLoadElementsLazily() {
    MyPage myPage = page(MyPage.class);
    myPage.wrongContent.h42.shouldNot(exist);
    myPage.h3.shouldNot(exist);
    List<MyForm> thisLinedShouldNotFail = myPage.wrongContent.forms;

    assertThatThrownBy(thisLinedShouldNotFail::size)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.tagName: form}")
      .getCause()
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
  }

  static class MyContent extends ElementsContainer {
    @FindBy(tagName = "form")
    private List<MyForm> forms;

    @FindBy(tagName = "h42")
    private SelenideElement h42;
  }

  static class MyForm extends ElementsContainer {
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
    private List<SelenideElement> h3ss;

    @FindBy(tagName = "h3")
    private List<WebElement> h3sss;
  }
}
