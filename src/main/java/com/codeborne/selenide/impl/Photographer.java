package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;

import java.util.Optional;

public interface Photographer {
  <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType);
}
