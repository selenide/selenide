package com.codeborne.selenide.junit;

import com.codeborne.selenide.logevents.SimpleReport;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * EXPERIMENTAL
 * 
 * Use with cautions! This API will likely be changed soon.
 *
 * @since Selenide 2.25
 */
public class TextReport extends TestWatcher {
  protected SimpleReport report = new SimpleReport();

  @Override
  protected void starting(Description description) {
    report.start();
  }

  @Override
  protected void finished(Description description) {
    report.finish(description.getDisplayName());
  }
}
