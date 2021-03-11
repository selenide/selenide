package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelenoidClipboardTest {

    @BeforeEach
    public void prepare() {
        Configuration.remote = "http://localhost:4444/wd/hub";
    }

    @Test
    public void getClipboardContent() {
        open("https://www.w3schools.com/howto/howto_js_copy_clipboard.asp");
        $("#myInput").should(Condition.attribute("value", "Hello World"));
        $("[onclick='myFunction()']").should(Condition.visible).click();
        assertEquals("Hello World", Selenide.clipboard().getText(), "clipboard content doesn't match");
    }

    @Test
    public void setClipboardContent(){
        open("https://www.w3schools.com/howto/howto_js_copy_clipboard.asp");
        Selenide.clipboard().setText("John Wick");
        assertEquals("John Wick", Selenide.clipboard().getText(), "clipboard content doesn't match");
    }

    @AfterEach
    public void tearDown(){
        closeWebDriver();
    }
}
