package com.codeborne.selenide.appium.demos;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class AndroidDriverWithDemos implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        File app = downloadApk();
        capabilities.setCapability(MobileCapabilityType.VERSION, "4.4.2");
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", "io.appium.android.apis");
        capabilities.setCapability("appActivity", ".ApiDemos");

        try {
            return new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private File downloadApk() {
        File apk = new File("build/ApiDemos-debug.apk");
        if (!apk.exists()) {
            String url = "https://github.com/appium/sample-code/blob/master/sample-code/apps/ApiDemos/bin/ApiDemos-debug.apk?raw=true";
            try (InputStream in = new URL(url).openStream()) {
                copyInputStreamToFile(in, apk);
            }
            catch (IOException e) {
                throw new AssertionError("Failed to download apk", e);
            }
        }
        return apk;
    }
}
