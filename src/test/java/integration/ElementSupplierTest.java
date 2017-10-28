package integration;


import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementSupplierTest extends IntegrationTest {

  @Test
  public void userCanCreateSelenideElementFromSupplier() throws Exception {
    openFile("page_for_supplier.html");
    SelenideElement element = $(this::getSecondInput);
    element.setValue("test");
    String text = element.getValue();

    assertEquals("Could not create element from supplier", "test", text);

  }

  @Test
  public void selenideDoesNotTriggerSupplierByWrapping() throws Exception {
    AtomicInteger counter = new AtomicInteger(0);
    Supplier<SelenideElement> get = () -> {
      counter.incrementAndGet();
      return $(By.id("id"));
    };
    SelenideElement element = $(get);
    assertThat(counter.get(), equalTo(0));
  }

  @Test
  public void selenideInvokesSupplierWhenStaleExceptionComing() throws Exception {
    AtomicInteger counter = new AtomicInteger(0);
    WebElement mock = mock(WebElement.class);
    when(mock.getAttribute("value")).thenThrow(new StaleElementReferenceException("Test error")).thenReturn("text");

    Supplier<WebElement> getElem = () -> {
      counter.incrementAndGet();
      return mock;
    };

    String text = $(getElem).getValue();

    assertEquals("Supplier was not triggered after exception as much as expected", 2, counter.get());
    assertEquals("Text from get value after exception is not equal expected text", "text", text);
  }

  private WebElement getSecondInput() {
    SelenideElement input = $(By.id("id1"));
    input.click();
    input.sendKeys(Keys.TAB);
    return WebDriverRunner.getWebDriver().switchTo().activeElement();
  }


}
