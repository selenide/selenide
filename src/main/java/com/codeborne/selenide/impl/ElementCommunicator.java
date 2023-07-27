package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface ElementCommunicator {

  List<String> texts(Driver driver, List<WebElement> elements);

  List<String> attributes(Driver driver, List<WebElement> elements, String attributeName);
}
