package com.codeborne.selenide;

/**
 * A `SelenideDriver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 */
final class ThreadLocalSelenideDriver extends SelenideDriver {
  ThreadLocalSelenideDriver() {
    super(new ThreadLocalSelenideConfig(), new StaticDriver());
  }

  private ThreadLocalSelenideConfig conf() {
    return (ThreadLocalSelenideConfig) config();
  }

  @Override
  public void close() {
    super.close();
    conf().reset();
  }

  public void open(Config config) {
    resetConfig(config);
    open();
  }

  void open(String relativeOrAbsoluteUrl, Config config) {
    resetConfig(config);
    open(relativeOrAbsoluteUrl);
  }

  private void resetConfig(Config config) {
    if (!conf().get().equals(config)) {
      close();
    }
    conf().set(config);
  }
}
