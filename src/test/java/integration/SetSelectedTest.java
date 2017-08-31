package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetSelectedTest extends IntegrationTest {
    @Before
    public void openTestPage() {
        openFile("page_with_multiple_select.html");
    }

    @Test
    public void testSelectOption() {
        SelenideElement element = $(By.xpath("//option[@value='master']"));
        element.setSelected(true);
        assertTrue(element.isSelected());

        element.setSelected(false);
        assertFalse(element.isSelected());
    }
}
