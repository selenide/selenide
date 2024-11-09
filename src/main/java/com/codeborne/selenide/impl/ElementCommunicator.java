package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface ElementCommunicator {

  List<String> texts(Driver driver, List<WebElement> elements);

  List<@Nullable String> attributes(Driver driver, List<WebElement> elements, String attributeName);
}
