package com.codeborne.selenide.testng;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Reports for every test method in the suite
 * Annotate any test class in your suite with {@code @Listeners({GlobalTextReport.class})}
 *
 * @since Selenide 3.6
 * @deprecated This class works exactly the same as {@link TextReport} since Selenide 6.7.0 - just use {@link TextReport} instead.
 */
@Deprecated
@ParametersAreNonnullByDefault
public class GlobalTextReport extends TextReport {
}
