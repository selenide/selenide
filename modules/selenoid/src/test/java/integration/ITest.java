package integration;

import static com.codeborne.selenide.Selenide.closeWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class ITest {
    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://borisosipov.github.io/test-page";
        Configuration.remote = "http://localhost:4444/wd/hub";
    }

    @BeforeEach
    void closeBrowser() {
        closeWebDriver();
    }

    @AfterEach
    @BeforeEach
    void clearProxySettings() {
        Configuration.fileDownload = FileDownloadMode.HTTPGET;
        Configuration.proxyEnabled = false;
    }
}
