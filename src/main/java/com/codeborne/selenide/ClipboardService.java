package com.codeborne.selenide;

import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import static com.codeborne.selenide.impl.Plugins.getDefaultPlugin;
import static com.google.common.base.Strings.isNullOrEmpty;


public class ClipboardService {

  private Driver driver;

  public ClipboardService(Driver driver) {
    this.driver = driver;
  }

  public Clipboard load() {
    if (isNullOrEmpty(driver.config().remote())) {
      return initClipboardWithDriver(getDefaultPlugin(Clipboard.class).getClass());
    } else {
      Clipboard implementation = StreamSupport.stream(ServiceLoader.load(Clipboard.class).spliterator(), false)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Remote clipboard plugin not defined!"));
      return initClipboardWithDriver(implementation.getClass());
    }
  }


  private Clipboard initClipboardWithDriver(Class<? extends Clipboard> clazz) {
    try {
      return clazz.getConstructor(Driver.class).newInstance(driver);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }
}
