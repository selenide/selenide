package integration;

import com.codeborne.selenide.Condition;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ClickRelativeTest extends IntegrationTest {
    @Before
    public void openTestPage() {
        openFile("page_with_relative_click_position.html");
    }

    @Test
    public void userCanClickElementWithOffsetPosition() {
        $(By.id("page")).click(123, 321);
        $(By.id("coords")).shouldHave(Condition.matchText("(123, 321)"));
    }
}
