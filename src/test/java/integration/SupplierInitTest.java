package integration;


import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class SupplierInitTest extends IntegrationTest{

    @Test
    public void userCanCreateSelenideElementFromSupplier() throws Exception {
        openFile("page_for_supplier.html");
        SelenideElement element = $(this::getSecondInput);
        element.setValue("test");
        String text = element.getValue();

        assertEquals("Could not create element from supplier", "test", text);

    }

    private WebElement getSecondInput() {
        SelenideElement input = $(By.id("id1"));
        input.click();
        input.sendKeys(Keys.TAB);
        return WebDriverRunner.getWebDriver().switchTo().activeElement();
    }


}
