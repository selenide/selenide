package integration;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.Test;

/**
 * Created by sergey on 11.02.17.
 */
public class AutomaticDriverManagementTest {

    @Test
    public void canStartChromeWithAutomaticDriver() throws Exception {
        Configuration.browser = WebDriverRunner.CHROME;
        Selenide.open("http://automation-remarks.com");
        Selenide.$$(".post").shouldHave(CollectionCondition.size(9));
    }

    @Test
    public void canStartPhantomJSWithAutomaticDriver() throws Exception {
        System.setProperty("phantomjs.binary.path", "");
        Configuration.browser = WebDriverRunner.MARIONETTE;
        Selenide.open("http://automation-remarks.com");
        Selenide.$$(".post").shouldHave(CollectionCondition.size(9));
    }
}
