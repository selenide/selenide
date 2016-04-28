package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.currentFrameUrl;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;

/**
 * Created by Fiatlux on 28.04.2016.
 */
public class BasicAuthTest extends IntegrationTest  {

    private String browserOriginalValue = "firefox";

    @Before
    public void setUp() {
        browserOriginalValue = Configuration.browser;
    }

    @After
    public void tearDown() {
        WebDriverRunner.closeWebDriver();
        Configuration.browser = browserOriginalValue;
    }

    @Test
    public void canPassBasicAuthInFirefox() {
        Configuration.browser = "firefox";
        Selenide.open("http://httpbin.org/basic-auth/user/passwd",
                "",
                "user",
                "passwd");
        $(By.tagName("pre")).waitUntil(visible, 10000);
        assertThat(source(), containsString("\"authenticated\": true,"));
    }

    @Test
    public void canPassBasicAuthInHtmlUnit() {
        Configuration.browser = "htmlunit";
        Selenide.open("http://httpbin.org/basic-auth/user/passwd",
                "",
                "user",
                "passwd");
        System.out.println(getWebDriver().getPageSource());
        assertThat(source(), containsString("\"authenticated\": true,"));
    }

    @Test @Ignore
    public void canPassBasicAuthInPhantom() {
        Configuration.browser = "phantomjs";
        Selenide.open("http://httpbin.org/basic-auth/user/passwd",
                "",
                "user",
                "passwd");
        $(By.tagName("pre")).waitUntil(visible, 10000);
        assertThat(source(), containsString("\"authenticated\": true,"));
    }

    @Test @Ignore
    public void canPassBasicAuthInChrome() {
        Configuration.browser = "chrome";
        Selenide.open("http://httpbin.org/basic-auth/user/passwd",
                "",
                "user",
                "passwd");
        $(By.tagName("pre")).waitUntil(visible, 10000);
        assertThat(source(), containsString("\"authenticated\": true,"));
    }

    @Test @Ignore
    public void canPassBasicAuthInIe() {
        Configuration.browser = "ie";
        Selenide.open("http://httpbin.org/basic-auth/user/passwd",
                "",
                "user",
                "passwd");
        assertThat(isAlertPresent(), is(false));
    }

    public boolean isAlertPresent() {
        try {
            Wait().until(alertIsPresent());
            return true;
        } catch (TimeoutException | NoAlertPresentException ex) {
            return false;
        }
    }
}
