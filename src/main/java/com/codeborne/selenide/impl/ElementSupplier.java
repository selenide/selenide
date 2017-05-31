package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static java.lang.Thread.currentThread;

/**
 * Created by Cok on 31.05.2017.
 */
public class ElementSupplier extends WebElementSource {

    private final Supplier<WebElement> howToGetElement;

    ElementSupplier(Supplier<WebElement> howToGetElement) {
       this.howToGetElement = howToGetElement;
    }

    public static SelenideElement wrap(Supplier<WebElement> howToGetElement) {
        return wrap(SelenideElement.class, howToGetElement);
    }

    @SuppressWarnings("unchecked")
    public static <T extends SelenideElement> T wrap(Class<T> clazz, Supplier<WebElement> howToGetElement) {
        return (T) Proxy.newProxyInstance(
                currentThread().getContextClassLoader(),
                new Class<?>[]{clazz},
                new SelenideElementProxy(new ElementSupplier(howToGetElement)));
    }

    @Override
    public WebElement getWebElement() {
        return this.howToGetElement.get();
    }

    @Override
    public String getSearchCriteria() {
        return howToGetElement.toString();
    }
}
