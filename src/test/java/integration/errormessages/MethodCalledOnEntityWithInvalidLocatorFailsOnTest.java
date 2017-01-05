package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.IntegrationTest;
import integration.helpers.HTMLBuilderForTestPreconditions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@Ignore
public class MethodCalledOnEntityWithInvalidLocatorFailsOnTest extends IntegrationTest {

    @Before
    public void openPage() {
        HTMLBuilderForTestPreconditions.Given.openedPageWithBody(
                "<ul>Hello to:",
                "<li class='the-expanse detective'>Miller <label>detective</label></li>",
                "<li class='the-expanse missing'>Julie Mao</li>",
                "</ul>"
        );
        Configuration.timeout = 0;
    }

    @Test
    public void shouldCondition_When$Element_WithInvalidLocator() {
        SelenideElement element = $("##invalid-locator");

        try {
            element.shouldHave(text("Miller"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));
        }
        //todo - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 76 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 696cd371-d06e-4b8d-bd9c-0883f70dd1e4
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void actionWithoutWaiting_When$Element__WithInvalidLocator() {
        SelenideElement element = $("##invalid-locator");

        try {
            element.exists();
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));
        }
        //todo - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 52 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: f69f7320-9d8b-45b5-a5c7-719ab7749558
            *** Element info: {Using=css selector, value=##invalid-locator}
        */

    }

    @Test
    public void shouldCondition_WhenCollectionElementByIndex_WithInvalidCollectionLocator() {
        SelenideElement element = $$("##invalid-locator").get(0);

        try {
            element.shouldHave(text("Miller"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));
        }
        //todo - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 63 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 257857cd-c69d-419d-99c6-6bf7ee2648af
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenCollectionElementByCondition_WithInvalidCollectionLocator() {
        SelenideElement element = $$("##invalid-locator").findBy(cssClass("the-expanse"));

        try {
            element.shouldBe(present);
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));
        }
        //todo  - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 54 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 1d0f1457-cbd4-45b3-b796-8d93c052bb1c
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenInnerElement_WithInvalidInnerElementLocator() {
        SelenideElement element = $("ul").find("##invalid-locator");

        try {
            element.shouldBe(present);
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));

        }
        //todo  - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 58 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 86fcc23f-66f6-40fe-9d2f-78c2b5fc07bd
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenInnerElement_WithInvalidOuterElementLocator() {
        SelenideElement element = $("##invalid-locator").find(".the-expanse");

        try {
            element.shouldBe(exactTextCaseSensitive("Miller"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified"));
        }
        //todo  - need to fix
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 81 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: b0422c41-203b-431d-ae76-9104adb588d5
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_When$$Collection_WithInvalidLocator() {
        ElementsCollection collection = $$("##invalid-locator");

        try {
            collection.shouldHave(exactTexts("Miller", "Julie Mao"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            //todo - need to fix
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified\n"));
        }
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 53 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: b6e66fca-fc9e-48dd-9103-0f7086859ddf
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void actionWithoutWaiting_WhenFilteredCollection_WithInvalidLocator() {
        ElementsCollection collection = $$("##invalid-locator").filter(cssClass("the-expanse"));

        try {
            collection.getTexts();
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            //todo - need to fix
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified\n"));
        }
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 224 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 7bbda627-bbb5-4cc3-a60e-d06f10babc2c
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenFilteredCollection_WithInvalidLocator() {
        ElementsCollection collection = $$("##invalid-locator").filter(cssClass("the-expanse"));

        try {
            collection.shouldHave(exactTexts("Miller", "Julie Mao"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            //todo - need to fix
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified\n"));
        }
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 62 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=46.0.1, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: efae40e6-44e8-4a2d-9987-80f6a54852c3
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenInnerCollection_WithOuterInvalidLocator() {
        ElementsCollection collection = $("##invalid-locator").findAll("li");

        try {
            collection.shouldHave(exactTexts("Miller", "Julie Mao"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            //todo - need to fix
            assertThat(expected.getMessage(), startsWith("Element not found {##invalid-locator}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(),
                    startsWith("The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:\n" +
                            "InvalidSelectorError: An invalid or illegal selector was specified\n"));
        }
        /*
            org.openqa.selenium.InvalidSelectorException: The given selector ##invalid-locator is either invalid or does not result in a WebElement. The following error occurred:
            InvalidSelectorError: An invalid or illegal selector was specified
            Command duration or timeout: 250 milliseconds
            For documentation on this error, please visit: http://seleniumhq.org/exceptions/invalid_selector_exception.html
            Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'
            System info: host: 'hp470g0', ip: '192.168.56.1', os.name: 'Windows 8.1', os.arch: 'x86', os.version: '6.3', java.version: '1.8.0_45'
            Driver info: org.openqa.selenium.firefox.FirefoxDriver
            Capabilities [{applicationCacheEnabled=true, rotatable=false, pageLoadStrategy=normal, handlesAlerts=true, databaseEnabled=true, version=44.0, platform=WINDOWS, nativeEvents=false, acceptSslCerts=true, webStorageEnabled=true, locationContextEnabled=true, browserName=firefox, takesScreenshot=true, javascriptEnabled=true, pageLoadingStrategy=normal, cssSelectorsEnabled=true}]
            Session ID: 5c78bba8-1c4d-4319-b9fb-710b9310ecc5
            *** Element info: {Using=css selector, value=##invalid-locator}
        */
    }

    @Test
    public void shouldCondition_WhenInnerCollection_WithInnerInvalidLocator() {
        ElementsCollection collection = $("ul").findAll("##invalid-locator");

        try {
            collection.shouldHave(exactTexts("Miller", "Julie Mao"));
            fail("Expected ElementNotFound");
        } catch (ElementNotFound expected) {
            //todo - need to fix
            assertThat(expected.getMessage(), startsWith("Element not found {ul}"));
            assertThat(expected.getScreenshot(), containsString(Configuration.reportsFolder));
            assertThat(expected.getCause(), instanceOf(InvalidSelectorException.class));
            assertThat(expected.getCause().getMessage(), containsString("##invalid-locator"));
        }
        /*
            Element not found {ul}
            Expected: exist

            Screenshot: file:/E:/julia/QA/selenide/build/reports/tests/firefox/integration/errormessages/MethodCalledOnEntityWithInvalidLocatorFailsOnTest/shouldCondition_WhenInnerCollection_WithInnerInvalidLocator/1471820076618.1.png
            Timeout: 6 s.
            Caused by: NoSuchElementException: Unable to locate element: {"method":"css selector","selector":"ul"}
        */
    }

}
