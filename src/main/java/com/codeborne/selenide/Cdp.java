package com.codeborne.selenide;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cdp {

  private WebDriver driver;
  private DevTools devTools;

  public Cdp(WebDriver driver) {
    this.driver = driver;
    devTools = ((HasDevTools) driver).getDevTools();
  }

  public void createSession() {
    devTools.createSession();
  }

  /**
   * Overrides the values of device screen dimensions
   * https://chromedevtools.github.io/devtools-protocol/tot/Emulation/#method-setDeviceMetricsOverride
   *
   * @param width       Overriding width value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param height      Overriding height value in pixels (minimum 0, maximum 10000000). 0 disables the override.
   * @param scaleFactor Overriding device scale factor value. 0 disables the override.
   */
  public void emulateMobileDevice(int width, int height, int scaleFactor) {
    Map<String, Object> params = new HashMap<>();
    params.put("width", width);
    params.put("height", height);
    params.put("deviceScaleFactor", scaleFactor);
    params.put("mobile", true);
    devTools.send(new Command<>("Emulation.setDeviceMetricsOverride", params));
  }

  /**
   * Clears the overridden device metrics.
   * https://chromedevtools.github.io/devtools-protocol/tot/Emulation/#method-clearDeviceMetricsOverride
   */
  public void clearEmulation() {
    devTools.send(new Command<>("Emulation.clearDeviceMetricsOverride", new HashMap<>()));
  }

}
