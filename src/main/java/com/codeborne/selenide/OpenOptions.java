package com.codeborne.selenide;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.BooleanSupplier;

import static java.util.Objects.requireNonNull;

/**
 * Options for opening a page: {@link SelenideDriver#open(String, OpenOptions)}.
 * <p>
 * Allows skipping the login in tests: log in once, save the browser state to a file, and restore it in other tests.
 * </p>
 *
 * <pre>{@code
 * open("/dashboard", withBrowserState("build/auth.json")
 *   .loggedInIf(() -> $("#logout").is(visible, Duration.ofSeconds(2)))
 *   .orLogin(() -> login("bob", "secret")));
 * }</pre>
 *
 * @see BrowserState
 * @since 7.19.0
 */
public final class OpenOptions {
  @Nullable
  private final Path file;
  @Nullable
  private final BrowserState browserState;
  @Nullable
  private final BooleanSupplier loggedInCheck;
  @Nullable
  private final Runnable login;

  private OpenOptions(@Nullable Path file, @Nullable BrowserState browserState,
                      @Nullable BooleanSupplier loggedInCheck, @Nullable Runnable login) {
    this.file = file;
    this.browserState = browserState;
    this.loggedInCheck = loggedInCheck;
    this.login = login;
  }

  /**
   * Restore browser state from the given file (in JSON format) before opening the page.
   * The file is not read immediately, but only when the page is being opened.
   *
   * @see BrowserSession#saveBrowserState(Path)
   */
  public static OpenOptions withBrowserState(String file) {
    return withBrowserState(Path.of(file));
  }

  /**
   * Restore browser state from the given file (in JSON format) before opening the page.
   * The file is not read immediately, but only when the page is being opened.
   *
   * @see BrowserSession#saveBrowserState(Path)
   */
  public static OpenOptions withBrowserState(Path file) {
    return new OpenOptions(file, null, null, null);
  }

  /**
   * Restore the given browser state before opening the page.
   */
  public static OpenOptions withBrowserState(BrowserState browserState) {
    return new OpenOptions(null, browserState, null, null);
  }

  /**
   * Check if user is logged in.
   * Used together with {@link #orLogin(Runnable)}.
   *
   * @param loggedInCheck returns true if user is logged in (after restoring the state and opening the page)
   */
  public OpenOptions loggedInIf(BooleanSupplier loggedInCheck) {
    return new OpenOptions(file, browserState, loggedInCheck, login);
  }

  /**
   * Log in if the file doesn't exist yet, or if the saved state is not valid anymore
   * (as reported by {@link #loggedInIf(BooleanSupplier)}).
   * After logging in, the browser state is saved to the file.
   *
   * @param login code that logs user in (opens login page, enters credentials etc.)
   */
  public OpenOptions orLogin(Runnable login) {
    if (loggedInCheck == null) {
      throw new IllegalStateException("Call loggedInIf() before orLogin(): otherwise expired state cannot be detected");
    }
    if (file == null) {
      throw new IllegalStateException("orLogin() requires browser state file: there is nowhere to save the state");
    }
    return new OpenOptions(file, browserState, loggedInCheck, login);
  }

  @Nullable
  Path file() {
    return file;
  }

  @Nullable
  BooleanSupplier loggedInCheck() {
    return loggedInCheck;
  }

  @Nullable
  Runnable login() {
    return login;
  }

  BrowserState browserState() {
    return browserState != null ? browserState : BrowserState.fromFile(requireNonNull(file));
  }

  @Override
  public String toString() {
    return "browser state " + (file != null ? file : requireNonNull(browserState).origin());
  }
}
