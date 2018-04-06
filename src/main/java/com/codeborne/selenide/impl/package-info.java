/**
 * Implementation details of Selenide library.
 *
 * Selenide is designed so that you can you override any single piece of its logic.
 * For example, to override the logic of taking screenshots one can override class:
 *
 * <pre>
 * Screenshots.screenshots = new ScreenShotLaboratory() {
 *   {@code @Override}
 *   protected File savePageImageToFile(String fileName, WebDriver webdriver) {
 *     // your custom logic
 *   }
 * };
 * </pre>
 *
 * NB! But "com.codeborne.selenide.impl" package is subject to change.
 * It's up to you to maintain your overridden logic.
 */
package com.codeborne.selenide.impl;
