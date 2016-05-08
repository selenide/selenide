package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.*;
import org.testng.*;

/**
 * Reports for every test method in the suite
 * Annotate any test class in your suite with {@code @Listeners({GlobalTextReport.class})}
 *
 * @since Selenide 3.6
 * <p>
 * Use either {@link TextReport} or {@link GlobalTextReport}, never both
 */
public class GlobalTextReport implements IInvokedMethodListener {
	protected SimpleReport report = new SimpleReport();

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		report.start();
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		report.finish(testResult.getName());
	}


}
