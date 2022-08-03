package integration.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CacheLookupPageObjectTest extends IntegrationTest {

  private SelectsPage pageWithSelects;

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    pageWithSelects = page(SelectsPage.class);
  }

  @Test
  void cachedWebElementShouldThrowStaleElementReferenceException() {
    assertThatCode(() -> pageWithSelects.webElement.getText()).doesNotThrowAnyException();

    removeElements(SelectsPage.WEB_ELEMENT_LOCATOR);

    assertThatExceptionOfType(ElementNotFound.class)
      .isThrownBy(() -> pageWithSelects.webElement.getText())
      .withRootCauseInstanceOf(StaleElementReferenceException.class);
  }

  @Test
  void cachedSelenideElementShouldThrowStaleElementReferenceException() {
    assertThatCode(() -> pageWithSelects.selenideElement.getText()).doesNotThrowAnyException();

    removeElements(SelectsPage.SELENIDE_ELEMENT_LOCATOR);

    assertThatExceptionOfType(ElementNotFound.class)
      .isThrownBy(() -> pageWithSelects.selenideElement.getText())
      .withRootCauseInstanceOf(StaleElementReferenceException.class);
  }

  @Test
  void cachedElementContainerShouldThrowStaleElementReferenceException() {
    assertThatCode(() -> pageWithSelects.elementsContainer.getSelf().getText()).doesNotThrowAnyException();

    removeElements(SelectsPage.ELEMENTS_CONTAINER_LOCATOR);

    assertThatExceptionOfType(ElementNotFound.class)
      .isThrownBy(() -> pageWithSelects.elementsContainer.getSelf().getText())
      .withRootCauseInstanceOf(StaleElementReferenceException.class);

    assertThatExceptionOfType(ElementShould.class)
      .isThrownBy(() -> pageWithSelects.elementsContainer.lastLogin.getText())
      .withMessageContaining(StaleElementReferenceException.class.getSimpleName());
  }

  @Test
  void cachedElementsCollectionElementsShouldThrowStaleElementReferenceException() {
    assertThat(pageWithSelects.elementsCollection.asDynamicIterable()).isNotEmpty();

    removeElements(SelectsPage.ELEMENTS_COLLECTION_LOCATOR);

    assertThat(pageWithSelects.elementsCollection.asDynamicIterable())
      .isNotEmpty()
      .allSatisfy(
        it -> assertThatExceptionOfType(ElementNotFound.class)
          .isThrownBy(it::getText)
          .withRootCauseInstanceOf(StaleElementReferenceException.class)
      );
  }

  @Test
  void cachedElementsContainerCollectionShouldThrowStaleElementReferenceException() {
    assertThat(pageWithSelects.elementsContainerCollection).isNotEmpty();

    removeElements(SelectsPage.ELEMENTS_CONTAINER_COLLECTION_LOCATOR);

    assertThat(pageWithSelects.elementsContainerCollection)
      .isNotEmpty()
      .allSatisfy(it -> {
        assertThatExceptionOfType(ElementNotFound.class)
          .isThrownBy(() -> it.getSelf().getText())
          .withRootCauseInstanceOf(StaleElementReferenceException.class);

        assertThatExceptionOfType(ElementShould.class)
          .isThrownBy(() -> it.firstName.getText())
          .withMessageContaining(StaleElementReferenceException.class.getSimpleName());
      });
  }

  private static void removeElements(String locator) {
    executeJavaScript("document.querySelectorAll(arguments[0]).forEach(it => it.remove());", locator);
  }

  private static class SelectsPage {

    static final String WEB_ELEMENT_LOCATOR = "#non-clickable-element";

    @CacheLookup
    @FindBy(css = WEB_ELEMENT_LOCATOR)
    WebElement webElement;

    static final String SELENIDE_ELEMENT_LOCATOR = "#clickable-element";

    @CacheLookup
    @FindBy(css = SELENIDE_ELEMENT_LOCATOR)
    SelenideElement selenideElement;

    static final String ELEMENTS_COLLECTION_LOCATOR = "h1";

    @CacheLookup
    @FindBy(css = ELEMENTS_COLLECTION_LOCATOR)
    ElementsCollection elementsCollection;

    static final String ELEMENTS_CONTAINER_LOCATOR = "#status";

    @CacheLookup
    @FindBy(css = ELEMENTS_CONTAINER_LOCATOR)
    StatusBlock elementsContainer;

    static final String ELEMENTS_CONTAINER_COLLECTION_LOCATOR = "#user-table tbody tr";

    @CacheLookup
    @FindBy(css = ELEMENTS_CONTAINER_COLLECTION_LOCATOR)
    List<UserInfo> elementsContainerCollection;
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
}
