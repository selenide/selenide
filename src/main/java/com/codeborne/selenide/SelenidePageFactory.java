package com.codeborne.selenide;

import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.lang.reflect.Field;

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see <a href="https://github.com/SeleniumHQ/selenium/wiki/PageObjects">Page Objects Wiki</a>
 */
public class SelenidePageFactory {

    public static void initElements(FieldDecorator decorator, Object page) {
        Class<?> proxyIn = page.getClass();
        while (proxyIn != Object.class) {
            proxyFields(decorator, page, proxyIn);
            proxyIn = proxyIn.getSuperclass();
        }
    }

    private static void proxyFields(FieldDecorator decorator, Object page, Class<?> proxyIn) {
        Field[] fields = proxyIn.getDeclaredFields();
        for (Field field : fields) {
            if(isInitialized(page, field)){
                continue;
            }
            Object value = decorator.decorate(page.getClass().getClassLoader(), field);
            if (value != null) {
                try {
                    field.setAccessible(true);
                    field.set(page, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static boolean isInitialized(Object page, Field field){
        try {
            field.setAccessible(true);
            return field.get(page) != null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
