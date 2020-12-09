package com.codeborne.selenide.testng.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vinogradov on 07.05.16.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

public @interface Report {


}
