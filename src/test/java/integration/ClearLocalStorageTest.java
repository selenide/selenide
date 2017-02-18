package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class ClearLocalStorageTest extends IntegrationTest {
    @Before
    public void addDataToLocalStorage() {
        executeJavaScript("localStorage.setItem('item1', 'item1');");
        executeJavaScript("localStorage.setItem('item2', 'item2');");
        Long storageLength = executeJavaScript("return localStorage.length;");
        assumeTrue(storageLength > 0);
    }

    @Test
    public void clearLocalStorageTest() {
        clearBrowserLocalStorage();
        Long storageLength = executeJavaScript("return localStorage.length;");
        assertTrue(storageLength == 0);
    }

}