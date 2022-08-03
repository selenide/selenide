package com.codeborne.selenide.testng.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vinogradov on 07.05.16.
 *
 * @deprecated This annotation is ignored since Selenide 6.7.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Deprecated
public @interface Report {
}
