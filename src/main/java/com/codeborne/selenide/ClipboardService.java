package com.codeborne.selenide;

import com.codeborne.selenide.impl.Plugins;
import com.google.common.base.Strings;

import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ClipboardService {

  private Driver driver;

  public ClipboardService(Driver driver) {
    this.driver = driver;
  }

  public Clipboard load() {
    Stream<Clipboard> implementations = StreamSupport.stream(ServiceLoader.load(Clipboard.class).spliterator(), false);
    return Strings.isNullOrEmpty(driver.config().remote())
      ? Plugins.getDefaultPlugin(Clipboard.class)
      : implementations.findFirst().orElseThrow(() -> new IllegalStateException("Remote Clipboard plugin not defined!"));
  }
}
