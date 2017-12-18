package integration;

import com.codeborne.selenide.Selenide;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.atBottom;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PageAtBottomTest extends IntegrationTest {
    @Before
    public void createScrollablePage() {
        openFile("empty.html");
        for (int i = 0; i < 200; i++) {
            Selenide.executeJavaScript(
                    "var span = document.createElement('li');" +
                            "document.body.appendChild(span);" +
                            "span.append('Element " + (i + 1) + "');"
            );
        }
    }

    @Test
    public void checkPageBoottomIsReached() {
        assertFalse("oops, we shouldn't be at the bottom yet! " + printScrollParams(), atBottom());
        Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");
        assertTrue("oops we should have reached the bottom already! " + printScrollParams(), atBottom());
    }

    private String printScrollParams() {
        return "\nwindow.scrollY=" + Selenide.executeJavaScript("return window.scrollY;") +
                "\nwindow.innerHeight=" + Selenide.executeJavaScript("return window.innerHeight;") +
                "\ndocument.body.scrollHeight=" + Selenide.executeJavaScript("return document.body.scrollHeight;");
    }


}
