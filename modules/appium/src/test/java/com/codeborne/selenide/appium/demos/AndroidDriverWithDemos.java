package com.codeborne.selenide.appium.demos;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class AndroidDriverWithDemos implements WebDriverProvider {
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        File app = downloadApk();
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(capabilities);
        desiredCapabilities.setCapability(MobileCapabilityType.VERSION, "4.4.2");
        desiredCapabilities.setCapability("automationName", "Appium");
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("app", app.getAbsolutePath());
        desiredCapabilities.setCapability("appPackage", "io.appium.android.apis");
        desiredCapabilities.setCapability("appActivity", ".ApiDemos");

        try {
            return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), desiredCapabilities);
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
