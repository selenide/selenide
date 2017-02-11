package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.*;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by sergey on 11.02.17.
 */
public class WebDriverBinaryManager{

    private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

    private static final String EDGE_PROPERTY = "webdriver.edge.driver";
    private static final String CHROME_PROPERTY = "webdriver.chrome.driver";
    private static final String IE_PROPERTY = "webdriver.ie.driver";
    private static final String OPERA_PROPERTY = "webdriver.opera.driver";
    private static final String PHANTOM_PROPERTY = "phantomjs.binary.path";
    private static final String MARIONETTE_PROPERTY = "webdriver.gecko.driver";

    static void setupBinaryPath() {
        try {
            if (isChrome() &&
                    isEmtyProperty(CHROME_PROPERTY)) {
                ChromeDriverManager.getInstance().setup();
            } else if (isEdge() &&
                    isEmtyProperty(EDGE_PROPERTY)) {
                EdgeDriverManager.getInstance().setup();
            } else if (isIE() && isEmtyProperty(IE_PROPERTY)) {
                InternetExplorerDriverManager.getInstance().setup();
            } else if (isOpera() &&
                    isEmtyProperty(OPERA_PROPERTY)) {
                OperaDriverManager.getInstance().setup();
            } else if (isPhantomjs() &&
                    isEmtyProperty(PHANTOM_PROPERTY)) {
                PhantomJsDriverManager.getInstance().setup();
            } else if (isMarionette() &&
                    isEmtyProperty(MARIONETTE_PROPERTY)) {
                FirefoxDriverManager.getInstance().setup();
            }
        } catch (final Exception ex) {
            log.warning("Problem to load driver binary for " + Configuration.browser);
        }
    }

    private static boolean isEmtyProperty(String property){
        return System.getProperty(property) == null;
    }
}
